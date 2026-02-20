#!/bin/bash
cowsay "Checks minikube stats, builds the java artifacts, loads the images into minikube and then applies the k8s yaml"
minikube status
./mvnw clean package
minikube image load fibonacci-java-web:latest
minikube image load fibonacci-java-jobrunr:latest
minikube kubectl -- apply -f minikube-k8s.yaml
cowsay "`minikube ip` access the exposed NodePorts"