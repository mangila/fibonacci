import {createClient} from "redis";
import dotenv from "dotenv";

dotenv.config();

if (!process.env.REDIS_URL) throw new Error("REDIS_URL is not defined")
if (!process.env.REDIS_QUEUE) throw new Error("REDIS_QUEUE is not defined")
if (!process.env.REDIS_CHANNEL) throw new Error("REDIS_CHANNEL is not defined")
if (!process.env.LOCAL_PUSH) throw new Error("LOCAL_PUSH is not defined")

export const REDIS_URL = process.env.REDIS_URL
export const REDIS_QUEUE = process.env.REDIS_QUEUE
export const REDIS_CHANNEL = process.env.REDIS_CHANNEL
export const LOCAL_PUSH = process.env.LOCAL_PUSH === "true"

export const REDIS_QUEUE_CLIENT = await createClient({
    url: REDIS_URL,
})
    .on("error", (err) => console.log("Redis Client Error", err))
    .connect();

export const REDIS_PUBLISHER_CLIENT = await createClient({
    url: REDIS_URL,
})
    .on("error", (err) => console.log("Redis Client Error", err))
    .connect();

export const REDIS_SUBSCRIBER_CLIENT = await createClient({
    url: REDIS_URL,
})
    .on("error", (err) => console.log("Redis Client Error", err))
    .connect();