import { useDynamicList, useMount, useUnmount, useUpdateEffect } from "ahooks";
import { useActionState, useRef, useState } from "react";
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
import { QueryForm } from "../../_components/QueryForm";
import { CountCard } from "../../_components/CountCard";
import {
  queryById,
  queryByStream,
  SSE_BASE_PATH,
  URL_BASE,
} from "../../_shared/shared";
import { ChannelCard } from "../../_components/ChannelCard";

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

interface Props {
  subscription: SseSubscription;
}

export const SseComponent = ({ subscription }: Props) => {
  const [status, setStatus] = useState<ConnectionStatus>("offline");
  const [state, formAction, isPending] = useActionState(handleSubmit, null);
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
        if (ev.event === "stream-start") {
          resetList([]);
          setStatus("streaming");
        }
        if (ev.event === "stream") {
          const data: FibonacciProjectionDto = JSON.parse(ev.data);
          push(data);
        }
        if (ev.event === "stream-end") {
          setStatus("open");
        }
        if (ev.event === "id") {
          const data: FibonacciDto = JSON.parse(ev.data);
          setModalData(data);
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
    console.log("mount");
    await openSseConnection();
  });

  useUnmount(() => {
    console.log("unmount");
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
  });

  useUpdateEffect(() => {
    if (state) {
      const offset = state.offset;
      const limit = state.limit;
      queryByStream({
        sseSubscription: subscription,
        option: {
          offset: parseInt(offset),
          limit: parseInt(limit),
        },
      });
    }
  }, [state]);

  return (
    <ErrorBoundary fallback={"the err is human..."}>
      <QueryForm isPending={isPending} payload={formAction} />
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
                queryById({
                  sseSubscription: subscription,
                  option: {
                    id: value.id,
                  },
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
