export type SseStatus = "offline" | "connecting" | "open" | "error";

export interface FibonacciData {
  id: number;
  result: string | null;
  precision: number;
}
