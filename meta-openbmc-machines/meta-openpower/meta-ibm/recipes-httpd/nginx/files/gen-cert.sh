#!/bin/sh

PEM="/etc/ssl/certs/nginx/cert.pem"

if [ ! -f $PEM ]; then
    openssl req -x509 -sha256 -newkey rsa:2048 -keyout $PEM -out $PEM \
    -days 3650 -subj "/O=openbmc-project.xyz/CN=localhost" \
    -nodes
fi
