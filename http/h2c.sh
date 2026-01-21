#!/bin/bash
cowsay "Test script to start a sse stream with Fibonacci Backend with H2C, (HTTP2 without TLS)"
curl --http2 -i -N "http://localhost:8080/api/v1/sse/mangila-livestream?streamKey=helloworld"
