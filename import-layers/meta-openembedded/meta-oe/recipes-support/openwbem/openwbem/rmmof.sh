#!/bin/sh
# 
# options:
# rmmof.sh <MOF_PATH> <NAMESPACE> <FILES>
#
# - or -
#
# options:
# loadmof.sh -n <NAMESPACE> <FILES> [...]
#
# The former is preserved for compatibility with Pegasus and 
# sblim providers.  The latter is preferred.  If $1 is "-n",
# the latter code path is executed.  Otherwise the former is 
# executed.

if [ "x$3" = "x" ]; then
    echo "Usage: $0 -n <NAMESPACE> <FILES> [...]"
    exit 1
fi

# get rid of "-n" arg
shift 

NS="$1"

shift 

DBDIR=/var/lib/openwbem
CIMOM_INIT=/etc/init.d/owcimomd
if [ "$YAST_IS_RUNNING" != "instsys" ] ; then
    $CIMOM_INIT status
    CIMOM_RUNNING=$?
fi
if [ "x$CIMOM_RUNNING" = "x0"  ]; then
  $CIMOM_INIT stop
fi
bkpdir=/tmp/owrep.bkp-$$
mkdir $bkpdir
cp -a $DBDIR $bkpdir/
echo "Compiling MOF files"
/usr/bin/owmofc -r -n $NS -d $DBDIR "$@" > /dev/null 2>&1
RVAL=$?
if [ "x$RVAL" != "x0" ]; then
  echo "MOF import failed!"
  rm -rf $DBDIR
  mv $bkpdir/openwbem $DBDIR
fi
rm -rf $bkpdir
if [ "x$CIMOM_RUNNING" = "x0"  ]; then
  $CIMOM_INIT start
fi
exit $RVAL

