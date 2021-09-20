instructions to execute

1) compile the files
    javac *.java
3) start the registy
    rmiregistry
5) start the server (2 options. one with cache and other without cache)
    a) java ServerSimulator -c
    b) Java ServerSimulator
7) start the load balancer
    java LoadBalancer
9) start the client (3 options. -s to inform server cache is enabled. -c to add cache to client)
    a ) java Client -s -c 
    b ) java Client -s
    c ) java Client



kill the port in mac:
1)  sudo lsof -i :<port_number> -> find PID
2)  sudo kill -9 PID

Documentation and screenshots available at /documentation
