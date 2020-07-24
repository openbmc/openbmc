#! /bin/bash

set -e

export SSTATE_CACHE_DIR=/home/srv/sstate/master

./poky/scripts/sstate-cache-management.sh -d -y
