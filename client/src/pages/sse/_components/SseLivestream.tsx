import { useEffect, useState } from "react";
import { useDynamicList } from "ahooks";
import type { FibonacciData, SseStatus } from "../../_types/types";

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
    <>
      {status}
      {list.map((value) => {
        return (
          <div key={value.id}>
            <p>
              {value.id} - {value.precision}
            </p>
          </div>
        );
      })}
    </>
  );
};
