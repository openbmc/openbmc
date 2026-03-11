#!/bin/sh
#
# find-execstack utility
# Copyright (c) 2007 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# This program looks for executable stacks
#

libdirs="/lib /lib64 /usr/lib /usr/lib64"
progdirs="/bin /sbin /usr/bin /usr/sbin /usr/libexec"
FOUND=0

# First param is which list to use, second is search pattern
scan () {
if [ "$1" = "1" ] ; then
	dirs=$libdirs
elif [ "$1" = "2" ] ; then
	dirs=$progdirs
fi

for d in $dirs ; do
	if [ ! -d $d ] ; then
		continue
	fi
	files=`/usr/bin/find $d -name "$2" -type f 2>/dev/null`
	for f in $files
	do
		FOUND_ONE=0
		stacks=`/usr/bin/eu-readelf -l $f 2>/dev/null | grep STACK`
		if [ x"$stacks" != "x" ] ; then
			perms=`echo $stacks | /bin/awk '{ print $7 }'`
			if [ x"$perms" != x -a "$perms" != "RW" ] ; then
				FOUND_ONE=1
			fi
		fi
		old_stacks=`echo $stacks | /bin/grep -v GNU_STACK`
		if [ x"$old_stacks" != "x" ] ; then
			FOUND_ONE=1
		fi
		heaps=`/usr/bin/eu-readelf -l $f 2>/dev/null | grep GNU_HEAP`
		if [ x"$heaps" != "x" ] ; then
			FOUND_ONE=1
		fi
		if [ $FOUND_ONE = 1 ] ; then
			printf "%-42s" $f
			rpm -qf --queryformat "%{SOURCERPM}" $f
			echo
			FOUND=1
		fi
	done
done
}

scan 1 '*.so'
scan 2 '*'

if [ $FOUND -eq 0 ] ; then
        # Nothing to report, just exit
        echo "No problems found" 1>&2
        exit 0
fi
exit 1


