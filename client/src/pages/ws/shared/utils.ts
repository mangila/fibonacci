import { Client } from "@stomp/stompjs";

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
