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

export interface SseStreamQuery {
  sseSubscription: SseSubscription;
  option: SseStreamOption;
}

export interface SseStreamOption {
  offset: number;
  limit: number;
}

export interface SseIdQuery {
  sseSubscription: SseSubscription;
  option: SseIdOption;
}

export interface SseIdOption {
  id: number;
}
