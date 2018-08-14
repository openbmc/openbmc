#!/bin/sh
# 
# options:
# loadmof.sh <MOF_PATH> <NAMESPACE> <FILES>
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

if [ "x$1" != "x-n" -a "x$1" != "x-v" ]; then
# OLD STYLE
if [ -f "/etc/init.d/owcimomd" ]; then
    /etc/init.d/owcimomd status 1>&2 > /dev/null
    if [ $? = "0" ]; then
        CIMOM_RUNNING="true"
    else
        CIMOM_RUNNING="false"
    fi
else
    exit 1
fi
if [ "$YAST_IS_RUNNING" = "instsys" ]; then
  CIMOM_RUNNING="false"
fi

CIMOM=$1
shift
case "$CIMOM" in
    pegasus)
        exit 0
    ;;
esac
MOF_PATH=$1
shift
NS=$1
shift

REPOSITORY="/var/lib/openwbem"
#tmp_dir=`mktemp -d -p /tmp openwbem.XXXXXX`
case "$CIMOM_RUNNING" in 
    true|false)
    while [ "$#" -gt 0 ]
    do
        echo "Loading $MOF_PATH/$1"
        #sed "s/cmpi:/cmpi::/g" $MOF_PATH/$1 > $tmp_dir/$1
        /usr/bin/owmofc -c -n  $NS -d $REPOSITORY $MOF_PATH/$1 > /dev/null 2>&1
        shift
    done
    ;;
esac
#rm -rf $tmp_dir
# END OLD STYLE

else
# NEW STYLE
if [ "x$3" = "x" ]; then
    echo "Usage: $0 -n <NAMESPACE> <FILES> [...]"
    exit 1
fi

if [ "x$1" = "x-v" ]; then
  VERBOSE=1
  shift
fi

# get rid of "-n" arg
shift 

NS="$1"

shift 

DBDIR=/var/lib/openwbem
LOGFILE=$DBDIR/loadmof.log
CIMOM_INIT=/etc/init.d/owcimomd
if [ "$YAST_IS_RUNNING" != "instsys" ] ; then
    $CIMOM_INIT status > /dev/null 2>&1
    CIMOM_RUNNING=$?
fi
if [ "x$CIMOM_RUNNING" = "x0"  ]; then
  $CIMOM_INIT stop > /dev/null 2>&1
fi
bkpdir=$DBDIR/backup-$$
mkdir $bkpdir
cp -a $DBDIR/*.{dat,ndx,lock} $bkpdir/
rm -f $LOGFILE.9
for i in 8 7 6 5 4 3 2 1 0; do
  let newI=$i+1
  if [ -f $LOGFILE.$i ]; then
    mv $LOGFILE.$i $LOGFILE.$newI
  fi
done
if [ -f $LOGFILE ]; then
  mv $LOGFILE $LOGFILE.0
fi
if [ "x$VERBOSE" = "x1" ]; then
  /usr/bin/owmofc -c -n $NS -d $DBDIR -s /usr/share/mof/cim-current "$@" 2>&1 | tee $LOGFILE
else
  /usr/bin/owmofc -c -n $NS -d $DBDIR -s /usr/share/mof/cim-current "$@" > $LOGFILE 2>&1
fi
RVAL=$?
if [ "x$RVAL" != "x0" ]; then
  echo "MOF import failed!  Check $LOGFILE for details."
  mv $bkpdir/* $DBDIR/
fi
rm -rf $bkpdir
if [ "x$CIMOM_RUNNING" = "x0"  ]; then
  $CIMOM_INIT start > /dev/null 2>&1
fi
exit $RVAL
fi

