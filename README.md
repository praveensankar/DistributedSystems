instructions to execute

1) compile the files
    javac *.java
3) start the registy
    rmiregistry
5) start the server
    java ServerSimulator -c
    Java ServerSimulator
7) start the load balancer
    java LoadBalancer
9) start the client
    java Client -s -c 
    java Client -s
    java Client



kill the port in mac:
1)  sudo lsof -i :<port_number> -> find PID
2)  sudo kill -9 PID
