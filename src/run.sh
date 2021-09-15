#!/bin/bash

killall rmiregistry
killall java ServerSimulator
killall java LoadBalancer

javac *.java

rmiregistry

