#!/bin/bash
cowsay "deletes the k8s deployments"
minikube kubectl -- delete -f minikube-k8s.yaml