#!/bin/sh

# signlibs.sh
#
# (c)2010 Wind River Systems, Inc.
#
# regenerates the .chk files for the NSS libraries that require it
# since the ones that are built have incorrect checksums that were
# calculated on the host where they really need to be done on the
# target

CHK_FILES=`ls /lib*/*.chk /usr/lib*/*.chk 2>/dev/null`
SIGN_BINARY=`which shlibsign`
for I in $CHK_FILES
do
       DN=`dirname $I`
       BN=`basename $I .chk`
       FN=$DN/$BN.so
       $SIGN_BINARY -i $FN
done
