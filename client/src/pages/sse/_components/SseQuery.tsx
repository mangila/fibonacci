import { useDynamicList, useUpdateEffect } from "ahooks";
import { useActionState, useEffect, useState } from "react";
import type {
  ConnectionStatus,
  FibonacciDto,
  FibonacciProjectionDto,
} from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { FibonacciCard } from "../../_components/FibonacciCard";
import { StatusCard } from "../../_components/StatusCard";
import { QueryForm } from "../../_components/QueryForm";
import { CountCard } from "../../_components/CountCard";
import { queryById, queryByList } from "../../_shared/shared";

interface Props {
  channel: string;
  url: URL;
}

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

export const SseQuery = ({ channel, url }: Props) => {
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const [state, formAction, isPending] = useActionState(handleSubmit, null);
  const { list, push, resetList } = useDynamicList<FibonacciProjectionDto>([]);
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

    sse.addEventListener("list", (e) => {
      resetList([]);
      const data: FibonacciProjectionDto[] = JSON.parse(e.data);
      data.map((value) => push(value));
    });

    sse.addEventListener("id", (e) => {
      const data: FibonacciDto = JSON.parse(e.data);
      setModalData(data);
    });

    sse.onopen = () => {
      setStatus("open");
    };

    sse.onerror = () => {
      setStatus("error");
      sse.close();
    };

    return () => {
      sse.close();
    };
  }, [channel, url]);

  useUpdateEffect(() => {
    if (state) {
      const offset = state.offset;
      const limit = state.limit;
      queryByList(channel, streamKey, offset, limit);
    }
  }, [state]);

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
              onMouseEnter={() => {
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
