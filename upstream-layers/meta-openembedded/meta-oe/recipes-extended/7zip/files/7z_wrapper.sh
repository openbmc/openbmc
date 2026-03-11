#!/bin/sh
realpath=`readlink -fn $0`
realdir=`dirname $realpath`
exec $realdir/7z.real "$@"
