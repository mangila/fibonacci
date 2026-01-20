import { Suspense, useEffect, useState } from "react";
import { useDynamicList } from "ahooks";
import type { FibonacciData, SseStatus } from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";

interface Props {
  url: URL;
}

export const SseLivestream = ({ url }: Props) => {
  const { list, push } = useDynamicList<FibonacciData>([]);
  const [status, setStatus] = useState<SseStatus>("offline");

  useEffect(() => {
    const streamKey = crypto.randomUUID();
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
      push(data);
    });

    sse.onerror = () => {
      sse.close();
      setStatus("error");
    };

    return () => {
      sse.close();
    };
  }, [url]);

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <Suspense fallback={"loading..."}>
        {status}
        <div className="grid grid-cols-12">
          {list.map((value) => {
            return (
              <div key={value.id}>
                ID : {value.id} Precision: {value.precision}
              </div>
            );
          })}
        </div>
      </Suspense>
    </ErrorBoundary>
  );
};
