#! /bin/sh

set -e -u

if [ $# = 0 ]; then
    echo No arguments as expected
    exit 0
else
    echo Unexpected arguments: $*
    exit 1
fi
