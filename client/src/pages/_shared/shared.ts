import axios from "axios";
import { Client } from "@stomp/stompjs";
import type { FibonacciCommand } from "../_types/types";

export const URL_BASE = new URL(import.meta.env.PUBLIC_FIBONACCI_API_URL);
export const SSE_BASE_PATH = "/api/v1/sse";
export const STOMP_URL = import.meta.env.PUBLIC_STOMP_URL;
export const SCHEDULER_URL_BASE = import.meta.env
  .PUBLIC_FIBONACCI_SCHEDULER_URL;
export const TEXT_DECODER = new TextDecoder("utf-8");

const BASE = new URL(URL_BASE);
const API_BASE = SSE_BASE_PATH;

export async function queryByList(
  channel: string,
  streamKey: string,
  offset: string,
  limit: string,
) {
  const url = new URL(API_BASE + `/${channel}/list`, BASE);
  url.searchParams.append("streamKey", streamKey);
  await axios.post(url.href, {
    offset: offset,
    limit: limit,
  });
}

export async function queryById(
  channel: string,
  streamKey: string,
  id: number,
) {
  const url = new URL(API_BASE + `/${channel}/id`, BASE);
  url.searchParams.append("streamKey", streamKey);
  url.searchParams.append("id", id.toString());
  await axios.get(url.href);
}

export async function enqueueFibonacciSequences(data: FibonacciCommand) {
  const url = new URL("api/v1/scheduler", SCHEDULER_URL_BASE);
  const response = await axios.post(url.href, {
    algorithm: data.algorithm,
    offset: data.offset,
    limit: data.limit,
    delayInMillis: data.delayInMillis,
  });
  return response;
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
