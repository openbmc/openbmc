#!/bin/sh
# Simple OE specific wrapper for bcc python tests

name=$1
kind=$2
cmd=$3
shift 3

case $kind in
    simple|sudo)
        $cmd "$@"
        ;;
    *)
        echo "Invalid kind $kind of test $name"
        exit 1
esac
