instructions to execute

1) compile the files
    javac LoadBalancer.java LoadBalancer.java Server.java ServerInterface.java Client.java
2) start the registy
    rmiregistry
3) start the server
    java Server
4) start the load balancer
    java LoadBalancer
5) start the client
    java Client



kill the port in mac:
1)  sudo lsof -i :<port_number> -> find PID
2)  sudo kill -9 PID
