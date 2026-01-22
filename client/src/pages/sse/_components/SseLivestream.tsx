import { useEffect, useState } from "react";
import { useDynamicList } from "ahooks";
import type {
  ConnectionStatus,
  FibonacciDto,
  FibonacciProjectionDto,
} from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { StatusCard } from "../../_components/StatusCard";
import { CountCard } from "../../_components/CountCard";
import { queryById } from "../../_shared/shared";

interface Props {
  channel: string;
  url: URL;
}

export const SseLivestream = ({ channel, url }: Props) => {
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const { list, push } = useDynamicList<FibonacciProjectionDto>([]);
  const [modalData, setModalData] = useState<FibonacciDto>({
    id: 0,
    sequence: 0,
    precision: 0,
    result: "",
  });
  const [streamKey] = useState(crypto.randomUUID());

  useEffect(() => {
    url.searchParams.append("streamKey", streamKey);
    const sse = new EventSource(url);

    sse.onopen = () => {
      setStatus("open");
    };

    sse.addEventListener("livestream", (e) => {
      const data: FibonacciProjectionDto[] = JSON.parse(e.data);
      data.map((value) => push(value));
    });

    sse.addEventListener("id", (e) => {
      const data: FibonacciDto = JSON.parse(e.data);
      setModalData(data);
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
                queryById(channel, streamKey, value.id);
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
