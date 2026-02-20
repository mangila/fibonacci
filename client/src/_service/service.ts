import axios from "axios";
import { Client } from "@stomp/stompjs";

export const URL_BASE = new URL(import.meta.env.PUBLIC_BASE_API_URL);
export const SSE_BASE_PATH = "/api/v1/sse";
export const REST_BASE_PATH = "/api/v1/rest";
export const STOMP_BASE_PATH = "/stomp";

export async function queryList(limit: string, offset: string) {
  const params = new URLSearchParams({
    limit: limit,
    offset: offset,
  });
  const url = new URL(REST_BASE_PATH + `/list`, URL_BASE);
  const response = await axios.get(url.href, {
    params: params,
  });
  return response.data;
}

export async function queryById(id: string) {
  const params = new URLSearchParams({
    id: id,
  });
  const url = new URL(REST_BASE_PATH + `/id`, URL_BASE);
  const response = await axios.get(url.href, {
    params: params,
  });
  return response.data;
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
