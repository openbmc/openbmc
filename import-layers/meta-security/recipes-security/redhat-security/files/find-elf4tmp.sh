#!/bin/sh
# find_elf4tmp utility
# Copyright (c) 2010-12 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# This script will search a directory and its subdirectories for all elf
# executables. It will then search for the use of the tmp directory. If it finds
# this is true, it will then check to see if XXX is being used which would
# indicate that the path is going to be randomized.

if [ $# -ge 2 ] ; then
	echo "Usage: find_elf4tmp [directory]" 1>&2
	exit 1
fi
if [ ! -x /usr/bin/strings ] ; then
	echo "Skipping due to missing /usr/bin/eu-strings utility"
	exit 1
fi
if [ -h /bin ] ; then
	DIRS="/usr/bin /usr/sbin /usr/libexec /usr/kerberos /usr/games /usr/lib /usr/lib64 /usr/local"
else
	DIRS="/bin /sbin /usr/bin /usr/sbin /usr/libexec /usr/kerberos /usr/games /lib /lib64 /usr/lib /usr/lib64 /usr/local"
fi
if [ $# -eq 1 ] ; then
	if [ -d "$1" ] ; then
		DIRS="$1"
	else
		echo "Option passed in was not a directory" 1>&2
		exit 1
	fi
fi

FOUND=0
for d in $DIRS
do
	if [ ! -d $d ] ; then
		continue
	fi
#	echo "Scanning files in $d..."
	for f in `/usr/bin/find $d -type f 2>/dev/null`
	do
		# Get just the elf executables
		testf=`echo $f | /usr/bin/file -n -f - 2>/dev/null | grep ELF`
		if [ x"$testf" != "x" ] ; then
			test_res=`/usr/bin/strings $f | /bin/grep '/tmp/' | /bin/egrep -v 'XX|/tmp/$|[ .,:]/tmp/'`
			if [ x"$test_res" = "x" ] ; then
				continue
			fi

			# Do further examination...
			syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' mkstemp@.*GLIBC| tempnam@.*GLIBC| tmpfile@.*GLIBC'`
			if [ x"$syms" != "x" ] ; then
				continue
			fi

			# Well its a bad one...out with it
			FOUND=1

			# Get the package
			RPM=`/bin/rpm -qf --queryformat "%{NAME}-%{VERSION}" $f 2>/dev/null | /bin/grep -v 'not owned' | /usr/bin/sort | /usr/bin/uniq`
			if [ x"$RPM" = "x" ] ; then
				RPM="<unowned>"
			fi

			# For each tmp string, output the line
			echo $test_res | /usr/bin/tr '\b' '\n' | /usr/bin/awk 'NF >= 1 { printf "%-46s\t%-30s\t%s\n", f, r, $1 }' r=$RPM f=$f
		fi
	done
done
if [ $FOUND -eq 0 ] ; then
	# Nothing to report, just exit
	echo "No problems found" 1>&2
	exit 0
fi
exit 1


