#!/bin/bash
cowsay "Start the Minikube cluster with some friendly defaults and spin up the dashboard"
minikube start --cpus 4 --memory 8192 --driver=docker
minikube addons enable dashboard
minikube dashboard