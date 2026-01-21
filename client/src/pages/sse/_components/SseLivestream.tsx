import { Suspense, useEffect, useState } from "react";
import { useDynamicList, useEventEmitter } from "ahooks";
import type { FibonacciData, SseStatus } from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { FibonacciCard } from "./FibonacciCard";
import { CountCard } from "./CountCard";
import { StatusCard } from "./StatusCard";

interface Props {
  channel: string;
  url: URL;
}

export const SseLivestream = ({ channel, url }: Props) => {
  const { list, push } = useDynamicList<FibonacciData>([]);
  const [status, setStatus] = useState<SseStatus>("offline");
  const [streamKey] = useState(crypto.randomUUID());
  const event$ = useEventEmitter<FibonacciData>();

  useEffect(() => {
    url.searchParams.append("streamKey", streamKey);
    const sse = new EventSource(url);

    sse.onopen = () => {
      setStatus("open");
    };

    sse.addEventListener("livestream", (e) => {
      const data: FibonacciData[] = JSON.parse(e.data);
      data.flatMap((value) => push(value));
    });

    sse.addEventListener("id", (e) => {
      const data: FibonacciData = JSON.parse(e.data);
      event$.emit(data);
    });

    sse.onerror = () => {
      sse.close();
      setStatus("error");
    };

    return () => {
      sse.close();
    };
  }, [channel, url]);

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <Suspense fallback={"loading..."}>
        <div className="flex justify-center m-4">
          <StatusCard status={status} />
          <CountCard count={list.length} />
        </div>
        <div className="grid grid-cols-3 md:grid-cols-12">
          {list.map((value) => {
            return (
              <div key={value.id}>
                <FibonacciCard
                  channel={channel}
                  streamKey={streamKey}
                  value={value}
                  event$={event$}
                />
              </div>
            );
          })}
        </div>
      </Suspense>
    </ErrorBoundary>
  );
};
