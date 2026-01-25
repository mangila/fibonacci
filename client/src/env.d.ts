/// <reference path="../.astro/types.d.ts" />
/// <reference types="astro/client" />
// src/env.d.ts
interface ImportMetaEnv {
  readonly PUBLIC_FIBONACCI_API_URL: string;
  readonly PUBLIC_FIBONACCI_SCHEDULER_URL: string
  readonly PUBLIC_STOMP_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
