import axios from "axios";
import { URL_BASE, SSE_BASE_PATH } from "./utils";

const BASE = new URL(URL_BASE);
const API_BASE = SSE_BASE_PATH;

export async function queryByList(
  channel: string,
  streamKey: string,
  offset: string,
  limit: string,
) {
  const url = new URL(API_BASE + `/${channel}/list`, BASE);
  url.searchParams.append("streamKey", streamKey);
  await axios.post(url.href, {
    offset: offset,
    limit: limit,
  });
}

export async function queryById(
  channel: string,
  streamKey: string,
  id: number,
) {
  const url = new URL(API_BASE + `/${channel}/id`, BASE);
  url.searchParams.append("streamKey", streamKey);
  url.searchParams.append("id", id.toString());
  await axios.get(url.href);
}
