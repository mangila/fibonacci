import axios from "axios";
import { Client } from "@stomp/stompjs";
import type { SseIdQuery, SseStreamQuery } from "../_types/types";

export const URL_BASE = new URL(import.meta.env.PUBLIC_FIBONACCI_API_URL);
export const SSE_BASE_PATH = "/api/v1/sse";
export const STOMP_URL = import.meta.env.PUBLIC_STOMP_URL;

const BASE = new URL(URL_BASE);
const SSE_API_BASE = SSE_BASE_PATH;

export async function queryByStream(query: SseStreamQuery) {
  const url = new URL(SSE_API_BASE + `/stream`, BASE);
  await axios.post(url.href, query);
}

export async function queryById(query: SseIdQuery) {
  const url = new URL(SSE_API_BASE + `/id`, BASE);
  await axios.post(url.href, query);
}

export function createStompClient(url: string) {
  const client = new Client({
    brokerURL: url,
    debug: function (str) {
      console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = function (frame) {
    // Do something; all subscriptions must be done in this callback.
    // This is needed because it runs after a (re)connect.
    console.log(frame);
  };

  client.onStompError = function (frame) {
    // Invoked when the broker reports an error.
    // Bad login/passcode typically causes an error.
    // Compliant brokers set the `message` header with a brief message; the body may contain details.
    // Compliant brokers terminate the connection after any error.
    console.log("Broker reported error: " + frame.headers["message"]);
    console.log("Additional details: " + frame.body);
  };
  return client;
}
