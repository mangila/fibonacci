import {
    LOCAL_PUSH,
    REDIS_CHANNEL,
    REDIS_PUBLISHER_CLIENT,
    REDIS_QUEUE,
    REDIS_QUEUE_CLIENT,
    REDIS_SUBSCRIBER_CLIENT
} from "./redis.js";

function fibonacci(n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}

async function main() {
    console.log("Starting Fibonacci Sequence Generator")
    if (LOCAL_PUSH) {
        await REDIS_QUEUE_CLIENT.lPush(REDIS_QUEUE, "10")
        await REDIS_SUBSCRIBER_CLIENT.subscribe(REDIS_CHANNEL, (message) => {
            console.log('Received:', message);
        })
    }
    while (true) {
        const enqueue = await REDIS_QUEUE_CLIENT.brPop(REDIS_QUEUE, 0)
            .catch(console.error);
        const n = enqueue.element;
        for (let i = 0; i < n; i++) {
            const fibSequence = fibonacci(i);
            await REDIS_PUBLISHER_CLIENT.publish(REDIS_CHANNEL, fibSequence.toString());
        }
    }
}

main().catch(console.error);