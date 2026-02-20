/// <reference path="../.astro/types.d.ts" />
/// <reference types="astro/client" />
// src/env.d.ts
interface ImportMetaEnv {
  readonly PUBLIC_BASE_API_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
