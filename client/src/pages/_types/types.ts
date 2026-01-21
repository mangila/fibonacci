export type ConnectionStatus = "offline" | "open" | "error";

export interface FibonacciData {
  id: number;
  result: string | null;
  precision: number;
}
