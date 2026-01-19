import axios from "axios";
import { URL_BASE, SSE_BASE_PATH } from "./utils";

export function unsubscribe(username: string) {
  const base = new URL(URL_BASE);
  const apiBase = SSE_BASE_PATH;
  const url = new URL(apiBase + `/subscribe/${username}`, base);

  fetch(url, {
    method: "DELETE",
    keepalive: true,
  }).then((response) => {
    if (response.ok) {
      console.log("ok");
    } else {
      console.log("err");
    }
  });
}
