#!/bin/bash
cowsay "Deletes the deployment and deletes the minikube cluster"
minikube kubectl -- delete -f minikube-k8s.yaml
minikube stop
minikube delete