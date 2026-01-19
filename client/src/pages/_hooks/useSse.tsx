import { useState } from "react";
import { useSafeState, useCreation, useUnmount } from "ahooks";
import type { Status } from "../_types/types";
import { SSE_BASE_PATH, URL_BASE } from "../_utils/utils";

export function useSse(username: string) {
  const [data, setData] = useSafeState(null);
  const [status, setStatus] = useState<Status>("offline");
  const sse = useCreation(() => {
    const base = new URL(URL_BASE);
    const apiBase = SSE_BASE_PATH;
    const url = new URL(apiBase + `/subscribe/${username}`, base);
    const sse = new EventSource(url);
    return sse;
  }, [username]);

  sse.onopen = () => {
    setStatus("open");
  };

  sse.addEventListener("livestream", (e) => {
    setData(e.data);
  });

  sse.addEventListener("list", (e) => {
    setData(e.data);
  });

  sse.addEventListener("id", (e) => {
    setData(e.data);
  });

  sse.onerror = () => {
    setStatus("error");
  };

  useUnmount(() => {
    if (sse) {
      sse.close();
      setStatus("offline");
      console.log("SSE closed");
    }
  });

  return { data, status };
}
