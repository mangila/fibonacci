#!/bin/bash
cowsay "Test script to start a sse stream with Fibonacci Backend with H2C, (HTTP2 without TLS)"
curl --http2 -i -N "http://localhost:8080/api/v1/sse" \
     -H "Content-Type: application/json" \
     -d '{"channel": "the-channel", "streamKey": "00000000-0000-0000-0000-000000000000"}'
