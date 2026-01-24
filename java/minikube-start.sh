#!/bin/bash
cowsay "Start Minikube with some defaults and spin up the dashboard"
minikube start --cpus 4 --memory 8192 --driver=docker
minikube addons enable dashboard
minikube dashboard