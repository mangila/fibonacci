import { useState } from "react";
import {
  useCreation,
  useDynamicList,
  useMount,
  useUnmount,
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
  queryById,
  STOMP_BASE_PATH,
  URL_BASE,
} from "../../../_service/service";

export const WsComponent = () => {
  const stompClient = useCreation(() => {
    const url = new URL(STOMP_BASE_PATH, URL_BASE);
    const client = createStompClient(url.href);
    client.onConnect = () => {
      setStatus("open");
      client.subscribe("/user/topic/fibonacci", (frame: IFrame) => {
        const data: FibonacciProjectionDto = JSON.parse(frame.body);
        push(data);
      });
    };
    client.onWebSocketError = () => {
      setStatus("error");
    };
    return client;
  }, []);
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const { list, push, resetList } = useDynamicList<FibonacciProjectionDto>([]);
  const [modalData, setModalData] = useState<FibonacciDto>({
    id: 0,
    sequence: 0,
    precision: 0,
    result: "",
  });

  useMount(() => {
    stompClient.activate();
  });

  useUnmount(async () => {
    await stompClient.deactivate();
  });

  return (
    <ErrorBoundary fallback={"the err is human..."}>
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
                queryById(value.id.toString())
                  .then((data) => {
                    setModalData(data);
                  })
                  .catch((err) => {
                    alert(err);
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
