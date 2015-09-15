#!/bin/bash

# Build performance regression test script
#
# Copyright 2011 Intel Corporation
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#
# DESCRIPTION
# This script is intended to be used in conjunction with "git bisect run"
# in order to find regressions in build time, however it can also be used
# independently. It cleans out the build output directories, runs a
# specified worker script (an example is test_build_time_worker.sh) under
# TIME(1), logs the results to TEST_LOGDIR (default /tmp) and returns a
# value telling "git bisect run" whether the build time is good (under
# the specified threshold) or bad (over it). There is also a tolerance
# option but it is not particularly useful as it only subtracts the
# tolerance from the given threshold and uses it as the actual threshold.
#
# It is also capable of taking a file listing git revision hashes to be
# test-applied to the repository in order to get past build failures that
# would otherwise cause certain revisions to have to be skipped; if a
# revision does not apply cleanly then the script assumes it does not
# need to be applied and ignores it.
#
# Please see the help output (syntax below) for some important setup
# instructions.
#
# AUTHORS
# Paul Eggleton <paul.eggleton@linux.intel.com>


syntax() {
    echo "syntax: $0 <script> <time> <tolerance> [patchrevlist]"
    echo ""
    echo "  script       - worker script file (if in current dir, prefix with ./)"
    echo "  time         - time threshold (in seconds, suffix m for minutes)"
    echo "  tolerance    - tolerance (in seconds, suffix m for minutes or % for"
    echo "                 percentage, can be 0)"
    echo "  patchrevlist - optional file listing revisions to apply as patches on top"
    echo ""
    echo "You must set TEST_BUILDDIR to point to a previously created build directory,"
    echo "however please note that this script will wipe out the TMPDIR defined in"
    echo "TEST_BUILDDIR/conf/local.conf as part of its initial setup (as well as your"
    echo "~/.ccache)"
    echo ""
    echo "To get rid of the sudo prompt, please add the following line to /etc/sudoers"
    echo "(use 'visudo' to edit this; also it is assumed that the user you are running"
    echo "as is a member of the 'wheel' group):"
    echo ""
    echo "%wheel ALL=(ALL) NOPASSWD: /sbin/sysctl -w vm.drop_caches=[1-3]"
    echo ""
    echo "Note: it is recommended that you disable crond and any other process that"
    echo "may cause significant CPU or I/O usage during build performance tests."
}

# Note - we exit with 250 here because that will tell git bisect run that
# something bad happened and stop
if [ "$1" = "" ] ; then
   syntax
   exit 250
fi

if [ "$2" = "" ] ; then
   syntax
   exit 250
fi

if [ "$3" = "" ] ; then
   syntax
   exit 250
fi

if ! [[ "$2" =~ ^[0-9][0-9m.]*$ ]] ; then
   echo "'$2' is not a valid number for threshold"
   exit 250
fi

if ! [[ "$3" =~ ^[0-9][0-9m.%]*$ ]] ; then
   echo "'$3' is not a valid number for tolerance"
   exit 250
fi

if [ "$TEST_BUILDDIR" = "" ] ; then
   echo "Please set TEST_BUILDDIR to a previously created build directory"
   exit 250
fi

if [ ! -d "$TEST_BUILDDIR" ] ; then
   echo "TEST_BUILDDIR $TEST_BUILDDIR not found"
   exit 250
fi

git diff --quiet
if [ $? != 0 ] ; then
    echo "Working tree is dirty, cannot proceed"
    exit 251
fi

if [ "$BB_ENV_EXTRAWHITE" != "" ] ; then
   echo "WARNING: you are running after sourcing the build environment script, this is not recommended"
fi

runscript=$1
timethreshold=$2
tolerance=$3

if [ "$4" != "" ] ; then
    patchrevlist=`cat $4`
else
    patchrevlist=""
fi

if [[ timethreshold == *m* ]] ; then
    timethreshold=`echo $timethreshold | sed s/m/*60/ | bc`
fi

if [[ $tolerance == *m* ]] ; then
    tolerance=`echo $tolerance | sed s/m/*60/ | bc`
elif [[ $tolerance == *%* ]] ; then
    tolerance=`echo $tolerance | sed s/%//`
    tolerance=`echo "scale = 2; (($tolerance * $timethreshold) / 100)" | bc`
fi

tmpdir=`grep "^TMPDIR" $TEST_BUILDDIR/conf/local.conf | sed -e 's/TMPDIR[ \t]*=[ \t\?]*"//' -e 's/"//'`
if [ "x$tmpdir" = "x" ]; then
    echo "Unable to determine TMPDIR from $TEST_BUILDDIR/conf/local.conf, bailing out"
    exit 250
fi
sstatedir=`grep "^SSTATE_DIR" $TEST_BUILDDIR/conf/local.conf | sed -e 's/SSTATE_DIR[ \t\?]*=[ \t]*"//' -e 's/"//'`
if [ "x$sstatedir" = "x" ]; then
    echo "Unable to determine SSTATE_DIR from $TEST_BUILDDIR/conf/local.conf, bailing out"
    exit 250
fi

if [ `expr length $tmpdir` -lt 4 ] ; then
    echo "TMPDIR $tmpdir is less than 4 characters, bailing out"
    exit 250
fi

if [ `expr length $sstatedir` -lt 4 ] ; then
    echo "SSTATE_DIR $sstatedir is less than 4 characters, bailing out"
    exit 250
fi

echo -n "About to wipe out TMPDIR $tmpdir, press Ctrl+C to break out...  "
for i in 9 8 7 6 5 4 3 2 1
do
    echo -ne "\x08$i"
    sleep 1
done
echo

pushd . > /dev/null

rm -f pseudodone
echo "Removing TMPDIR $tmpdir..."
rm -rf $tmpdir
echo "Removing TMPDIR $tmpdir-*libc..."
rm -rf $tmpdir-*libc
echo "Removing SSTATE_DIR $sstatedir..."
rm -rf $sstatedir
echo "Removing ~/.ccache..."
rm -rf ~/.ccache

echo "Syncing..."
sync
sync
echo "Dropping VM cache..."
#echo 3 > /proc/sys/vm/drop_caches
sudo /sbin/sysctl -w vm.drop_caches=3 > /dev/null

if [ "$TEST_LOGDIR" = "" ] ; then
    logdir="/tmp"
else
    logdir="$TEST_LOGDIR"
fi
rev=`git rev-parse HEAD`
logfile="$logdir/timelog_$rev.log"
echo -n > $logfile

gitroot=`git rev-parse --show-toplevel`
cd $gitroot
for patchrev in $patchrevlist ; do
    echo "Applying $patchrev"
    patchfile=`mktemp`
    git show $patchrev > $patchfile
    git apply --check $patchfile &> /dev/null
    if [ $? != 0 ] ; then
        echo " ... patch does not apply without errors, ignoring"
    else
        echo "Applied $patchrev" >> $logfile
        git apply $patchfile &> /dev/null
    fi
    rm $patchfile
done

sync
echo "Quiescing for 5s..."
sleep 5

echo "Running $runscript at $rev..."
timeoutfile=`mktemp`
/usr/bin/time -o $timeoutfile -f "%e\nreal\t%E\nuser\t%Us\nsys\t%Ss\nmaxm\t%Mk" $runscript 2>&1 | tee -a $logfile
exitstatus=$PIPESTATUS

git reset --hard HEAD > /dev/null
popd > /dev/null

timeresult=`head -n1 $timeoutfile`
cat $timeoutfile | tee -a $logfile
rm $timeoutfile

if [ $exitstatus != 0 ] ; then
    # Build failed, exit with 125 to tell git bisect run to skip this rev
    echo "*** Build failed (exit code $exitstatus), skipping..." | tee -a $logfile
    exit 125
fi

ret=`echo "scale = 2; $timeresult > $timethreshold - $tolerance" | bc`
echo "Returning $ret" | tee -a $logfile
exit $ret

