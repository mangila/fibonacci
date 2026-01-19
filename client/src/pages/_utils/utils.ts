export const URL_BASE = new URL(import.meta.env.PUBLIC_FIBONACCI_API_URL);
export const SSE_BASE_PATH = "/api/v1/sse/fibonacci";

export function getElementByIdOrThrow(id: string): HTMLElement {
  const element = document.getElementById(id);
  if (!element) {
    throw new Error(`Element with id "${id}" not found`);
  }
  return element;
}
