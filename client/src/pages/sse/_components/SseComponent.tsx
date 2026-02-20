import { useDynamicList, useMount, useUnmount } from "ahooks";
import { useRef, useState } from "react";
import { fetchEventSource } from "@microsoft/fetch-event-source";
import type {
  ConnectionStatus,
  FibonacciDto,
  FibonacciProjectionDto,
  SseSubscription,
} from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { StatusCard } from "../../_components/StatusCard";
import { CountCard } from "../../_components/CountCard";
import { ChannelCard } from "../../_components/ChannelCard";
import { queryById, SSE_BASE_PATH, URL_BASE } from "../../../_service/service";

interface Props {
  subscription: SseSubscription;
}

export const SseComponent = ({ subscription }: Props) => {
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const { list, push, resetList } = useDynamicList<FibonacciProjectionDto>([]);
  const [modalData, setModalData] = useState<FibonacciDto>({
    id: 0,
    sequence: 0,
    precision: 0,
    result: "",
  });
  const abortControllerRef = useRef<AbortController>(null);

  const openSseConnection = async () => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    const controller = new AbortController();
    abortControllerRef.current = controller;

    const sseUrl = new URL(SSE_BASE_PATH + "/subscribe", URL_BASE);

    await fetchEventSource(sseUrl.href, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      signal: controller.signal,
      body: JSON.stringify(subscription),
      onopen() {
        setStatus("open");
      },
      onmessage(ev) {
        console.log(ev);
        if (ev.event === "stream") {
          const data: FibonacciProjectionDto = JSON.parse(ev.data);
          push(data);
        }
      },
      onclose() {
        setStatus("offline");
      },
      onerror(err) {
        console.error(err);
        setStatus("error");
      },
    });
  };

  useMount(async () => {
    await openSseConnection();
  });

  useUnmount(() => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
  });

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <div className="flex justify-center m-4">
        <ChannelCard subscription={subscription} />
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
