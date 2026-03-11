#!/bin/sh
#
# find-chroot utility
# Copyright (c) 2011 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# This program looks for apps that use chroot(2) without using chdir(2)
#
# To save to file: ./find-chroot | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]//g" | tee findings.txt

libdirs="/lib /lib64 /usr/lib /usr/lib64"
progdirs="/bin /sbin /usr/bin /usr/sbin /usr/libexec"
FOUND=0

# First param is which list to use, second is search pattern
scan () {
if [ "$1" = "1" ] ; then
	dirs=$libdirs
elif [ "$1" = "2" ] ; then
	dirs=$progdirs
elif [ "$1" = "3" ] ; then
	dirs=$3
fi

for d in $dirs ; do
	if [ ! -d $d ] ; then
		continue
	fi
	files=`/usr/bin/find $d -name "$2" -type f 2>/dev/null`
	for f in $files
	do
		syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' chroot@.*GLIBC'`
		if [ x"$syms" != "x" ] ; then
			syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' chdir@.*GLIBC'`
			if [ x"$syms" = "x" ] ; then
				if [ $FOUND = 0 ]  ; then
					printf "%-44s%s\n" "FILE" " PACKAGE"
					FOUND=1
				fi
				# Red
				printf "\033[31m%-44s\033[m" $f
				#rpm -qf --queryformat "%{NAME}-%{VERSION}" $f
				rpm -qf --queryformat " %{SOURCERPM}" $f
				echo
			else
				# One last test to see if chdir is within 3
				# lines of chroot
				syms=`objdump -d $f | egrep callq | egrep 'chroot@plt' -A2 | egrep 'chroot|chdir'`
				if [ x"$syms" = "x" ] ; then
					syms=`echo $f | egrep -v 'libc-2|libc.so'`
					if [ x"$syms" != "x" ] ; then
						if [ $FOUND = 0 ]  ; then
							printf "%-44s%s\n" "FILE" "PACKAGE"
							FOUND=1
						fi
						printf "\033[31m%-44s\033[m" $f
						rpm -qf --queryformat " %{SOURCERPM}" $f
						echo
					fi
				fi
			fi
		fi
	done
done
}

if [ $# -eq 1 ] ; then
	if [ -d $1 ] ; then
		scan 3 '*' $1
	else
		echo "Input is not a directory"
		exit 1
	fi
else
	scan 2 '*'
	scan 1 '*.so'
fi

if [ $FOUND -eq 0 ] ; then
        # Nothing to report, just exit
        echo "No problems found" 1>&2
        exit 0
fi
exit 1


