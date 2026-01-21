import { useDynamicList, useEventEmitter } from "ahooks";
import { Suspense, useActionState, useEffect, useState } from "react";
import type { FibonacciData, SseStatus } from "../../_types/types";
import { ErrorBoundary } from "react-error-boundary";
import { CountCard } from "./CountCard";
import { FibonacciCard } from "./FibonacciCard";
import { StatusCard } from "./StatusCard";
import { queryByList } from "../../_utils/api";

interface Props {
  channel: string;
  url: URL;
}

async function handleSubmit(prevState, formData: FormData) {
  const offsetData = formData.get("offset");
  if (offsetData === null) {
    throw new Error("err");
  }
  const limitData = formData.get("limit");
  if (limitData === null) {
    throw new Error("err");
  }
  const channelData = formData.get("channel");
  if (channelData === null) {
    throw new Error("err");
  }
  const streamKeyData = formData.get("streamKey");
  if (streamKeyData === null) {
    throw new Error("err");
  }

  const channel = channelData.toString();
  const streamKey = streamKeyData.toString();
  const offset = offsetData.toString();
  const limit = limitData.toString();

  await queryByList(channel, streamKey, offset, limit);
  return { offset, limit };
}

export const SseQuery = ({ channel, url }: Props) => {
  const [, formAction, isPending] = useActionState(handleSubmit, null);
  const { list, push, resetList } = useDynamicList<FibonacciData>([]);
  const [status, setStatus] = useState<SseStatus>("offline");
  const [streamKey] = useState(crypto.randomUUID());
  const event$ = useEventEmitter<FibonacciData>();

  useEffect(() => {
    url.searchParams.append("streamKey", streamKey);
    const sse = new EventSource(url);

    sse.onopen = () => {
      setStatus("open");
    };

    sse.addEventListener("list", (e) => {
      resetList([]);
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
      <form action={formAction} className="flex justify-center m-4">
        <input
          type="number"
          name="offset"
          placeholder="Offset"
          className="input validator"
          min="0"
          required
        />
        <input
          type="number"
          name="limit"
          placeholder="Limit"
          className="input validator"
          min="1"
          max="1000"
          required
        />
        <input type="hidden" name="channel" value={channel} required />
        <input type="hidden" name="streamKey" value={streamKey} required />
        <input type="submit" disabled={isPending} />
      </form>
      <div className="divider"></div>
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
    </ErrorBoundary>
  );
};
