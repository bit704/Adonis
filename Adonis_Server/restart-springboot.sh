source /etc/profile
killall java
nohup java -jar /root/adonis-0.0.1-SNAPSHOT.jar > nohup.log 2>&1 &
