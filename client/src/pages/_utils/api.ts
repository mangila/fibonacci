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
