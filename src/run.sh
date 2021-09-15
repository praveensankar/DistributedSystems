#!/bin/bash

killall rmiregistry
killall java ServerSimulator
killall java LoadBalancer

javac *.java

gnome-terminal rmiregistry
gnome-terminal java ServerSimulator
java LoadBalancer
# java Client

# killall rmiregistry
# killall java ServerSimulator
# killall java LoadBalancer
