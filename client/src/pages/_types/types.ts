export type ConnectionStatus = "offline" | "open" | "streaming" | "error";

export enum FibonacciAlgorithm {
  Recursive = "RECURSIVE",
  Iterative = "ITERATIVE",
  FastDoubling = "FAST_DOUBLING",
}

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

export interface SseSubscription {
  channel: string;
  username: string;
}
