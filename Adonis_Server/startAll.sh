source /etc/profile
killall java

nohup java -jar /root/EurekaServer-0.0.1-SNAPSHOT.jar > EurekaServer.log 2>&1 &

nohup java -jar /root/AdonisBase-0.0.1-SNAPSHOT.jar > AdonisBase.log 2>&1 &

nohup java -jar /root/AdonisWebsocket-0.0.1-SNAPSHOT.jar > AdonisWebsocket.log 2>&1 &