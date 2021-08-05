#!/bin/bash
curl https://jinhx.cc/api/actuator/shutdown -X POST
sleep 20
docker rm blog
docker rmi jinhx128/blog:latest
docker run -dit --name blog -v /usr/local/docker/blog/log:/usr/local/project/blog/log -v /etc/localtime:/etc/localtime:ro -p 8800:8800 -p 9999:9999 -p 12138:12138 -p 465:465 jinhx128/blog:latest