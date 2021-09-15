#!/bin/bash

killall rmiregistry
killall java ServerSimulator
killall java LoadBalancer

javac *.java

rmiregistry
# java Client

# killall rmiregistry
# killall java ServerSimulator
# killall java LoadBalancer
