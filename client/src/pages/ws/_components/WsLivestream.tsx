import { useState } from "react";
import { createStompClient } from "../shared/utils";
import { useCreation, useDynamicList, useMount, useUnmount } from "ahooks";
import type { IFrame } from "@stomp/stompjs";
import { ErrorBoundary } from "react-error-boundary";
import type { ConnectionStatus, FibonacciData } from "../../_types/types";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { StatusCard } from "../../_components/StatusCard";
import { CountCard } from "../../_components/CountCard";
import { STOMP_URL, TEXT_DECODER } from "../../_utils/utils";

export const WsLivestream = () => {
  const stompClient = useCreation(() => {
    const client = createStompClient(STOMP_URL);
    client.onConnect = () => {
      setStatus("open");
      client.subscribe("/topic/livestream", (frame: IFrame) => {
        const decoded: string = TEXT_DECODER.decode(frame.binaryBody);
        const data: FibonacciData[] = JSON.parse(decoded);
        data.map((value) => push(value));
      });
      client.subscribe("/user/queue/fibonacci/id", (frame: IFrame) => {
        const decoded: string = TEXT_DECODER.decode(frame.binaryBody);
        const data: FibonacciData = JSON.parse(decoded);
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
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const { list, push } = useDynamicList<FibonacciData>([]);
  const [modalData, setModalData] = useState<FibonacciData>({
    id: 0,
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
              onMouseEnter={() => {
                stompClient.publish({
                  destination: "/app/fibonacci/id",
                  body: value.id.toString(),
                });
              }}
            >
              <FibonacciCard id={value.id} data={modalData} />
            </div>
          );
        })}
      </div>
    </ErrorBoundary>
  );
};
