import axios from "axios";
import { URL_BASE, SSE_BASE_PATH } from "./utils";

const BASE = new URL(URL_BASE);
const API_BASE = SSE_BASE_PATH;

export async function queryForList(username: string) {
  const url = new URL(API_BASE + `/${username}`, BASE);

  const response = await axios.post(url.href, {
    offset: 0,
    limit: 1000,
  });

  return response.data;
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
