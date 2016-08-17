FROM debian

MAINTAINER amy.fong@windriver.com

RUN apt-get update && apt-get install figlet

ENTRYPOINT [ "/usr/bin/figlet", "hi" ]
