![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)

# fibonacci

Run a fibonacci sequence and publish every sequence to a channel

* Only implemented to being used with Elixir Phoenix LiveView

## Env

* REDIS_URL=redis://localhost:6379 - Redis URL
* REDIS_QUEUE=fibonacci - Queue to publish to
* REDIS_CHANNEL=fibonacci-channel - Channel to publish to
* LOCAL_PUSH=false - Set to true to push to the channel automatically