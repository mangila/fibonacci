import { useActionState, useState } from "react";
import {
  useCreation,
  useDynamicList,
  useMount,
  useUnmount,
  useUpdateEffect,
} from "ahooks";
import type { IFrame } from "@stomp/stompjs";
import { ErrorBoundary } from "react-error-boundary";
import type {
  ConnectionStatus,
  FibonacciDto,
  FibonacciProjectionDto,
} from "../../_types/types";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { StatusCard } from "../../_components/StatusCard";
import { CountCard } from "../../_components/CountCard";
import {
  createStompClient,
  STOMP_URL,
  TEXT_DECODER,
} from "../../_shared/shared";
import { QueryForm } from "../../_components/QueryForm";

function handleSubmit(_, formData: FormData) {
  const offsetData = formData.get("offset");
  if (offsetData === null) {
    throw new Error("err");
  }
  const limitData = formData.get("limit");
  if (limitData === null) {
    throw new Error("err");
  }

  const offset = offsetData.toString();
  const limit = limitData.toString();

  return { offset, limit };
}

export const WsQuery = () => {
  const stompClient = useCreation(() => {
    const client = createStompClient(STOMP_URL);
    client.onConnect = () => {
      setStatus("open");
      client.subscribe("/user/queue/stream", (frame: IFrame) => {
        const data: FibonacciProjectionDto = JSON.parse(frame.body);
        push(data);
      });
      client.subscribe("/user/queue/id", (frame: IFrame) => {
        const data: FibonacciDto = JSON.parse(frame.body);
        setModalData(data);
      });
      client.subscribe("/user/queue/errors", (frame: IFrame) => {
        const decoded: string = TEXT_DECODER.decode(frame.binaryBody);
        console.error(decoded);
      });
    };
    client.onWebSocketError = () => {
      setStatus("error");
    };
    return client;
  }, []);
  const [state, formAction, isPending] = useActionState(handleSubmit, null);
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const { list, push, resetList } = useDynamicList<FibonacciProjectionDto>([]);
  const [modalData, setModalData] = useState<FibonacciDto>({
    id: 0,
    sequence: 0,
    precision: 0,
    result: "",
  });

  useUpdateEffect(() => {
    if (state) {
      stompClient.publish({
        destination: "/app/fibonacci/list",
        body: JSON.stringify({
          offset: state.offset,
          limit: state.limit,
        }),
      });
    }
  }, [state]);

  useMount(() => {
    stompClient.activate();
  });

  useUnmount(async () => {
    await stompClient.deactivate();
  });

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <QueryForm isPending={isPending} payload={formAction} />
      <div className="flex justify-center m-4">
        <StatusCard status={status} />
        <CountCard count={list.length} />
      </div>
      <div className="grid grid-cols-3 md:grid-cols-12">
        {list.map((value) => {
          return (
            <div
              key={value.id}
              onClick={() => {
                stompClient.publish({
                  destination: "/app/fibonacci/id",
                  body: value.id.toString(),
                });
              }}
            >
              <FibonacciCard
                id={value.id}
                sequence={value.sequence}
                data={modalData}
              />
            </div>
          );
        })}
      </div>
    </ErrorBoundary>
  );
};
