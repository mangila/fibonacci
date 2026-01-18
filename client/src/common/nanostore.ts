import { map } from "nanostores";

export const INIT_VALUE = { selected: false, dataSource: undefined };

export const $ws = map(INIT_VALUE);
export const $sse = map(INIT_VALUE);
