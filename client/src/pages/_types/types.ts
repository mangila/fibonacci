export type ConnectionStatus = "offline" | "open" | "error";

export interface FibonacciDto {
  id: number;
  sequence: number;
  result: string;
  precision: number;
}

export interface FibonacciProjectionDto {
  id: number;
  sequence: number;
  precision: number;
}
