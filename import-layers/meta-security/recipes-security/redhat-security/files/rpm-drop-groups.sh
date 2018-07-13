#!/bin/sh
# rpm-drop-groups
#
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
# Given an rpm, it will look at each file to check if it tries to change
# group and user credentials. If so, it further tries to determine if
# it also calls setgroups or initgroups. To correctly change groups, the
# program must drop supplemntal groups. Programs are classified into: n/a
# meaning no group dropping occurs, yes its done correctly, and no meaning
# there seems to be a problem.
#
# If the --all option is given, it will generate a list of rpms and then
# summarize the rpm's state. For yes, then all files are in the expected
# state. Just one program failing can turn the package's summary to no.
# Re-run passing that package (instead of --all) for the details.
#
# To save to file: ./rpm-drop-groups | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]//g" | tee output.txt

VERSION="0.1"

usage () {
	echo "rpm-drop-groups [--all|<rpmname>|--version]"
	exit 0
}

if [ "$1" = "--help" -o $# -eq 0 ] ; then
	usage
fi
if [ "$1" = "--version" ] ; then
	echo "rpm-drop-groups $VERSION"
	exit 0
fi
if [ "$1" = "--all" ] ; then
	MODE="all"
else
	MODE="single"
fi

do_one () {
if ! rpm -q $1 >/dev/null 2>&1 ; then
	if [ "$MODE" = "single" ] ; then
		echo "$1 is not installed"
		exit 1
	else
		echo "not installed"
		return
	fi
fi
files=`rpm -ql $1`

for f in $files
do
	if [ ! -f $f ] ; then
		continue
	fi
	if ! file $f | grep -q 'ELF'; then
		continue
	fi

	CORRECT="n/a"
	syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' setgid@.*GLIBC| setegid@.*GLIBC| setresgid@.*GLIBC'`
	if [ x"$syms" != "x" ] ; then
		CORRECT="yes"
		syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' setuid@.*GLIBC| seteuid@.*GLIBC| setresuid@.*GLIBC'`
		if [ x"$syms" != "x" ] ; then
			syms=`/usr/bin/readelf -s $f 2>/dev/null | egrep ' setgroups@.*GLIBC| initgroups@.*GLIBC'`
			if [ x"$syms" = "x" ] ; then
				syms=`find $f \( -perm -004000 -o -perm -002000 \) -type f -print`
				if [ x"$syms" = "x" ] ; then
					CORRECT="no"
				fi
			fi
		fi
	fi

	# OK, ready for the output
	if [ "$MODE" = "single" ] ; then
		printf "%-60s  " $f
		if [ "$CORRECT" = "yes" ] ; then
			printf "\033[32m%-7s\033[m  " $CORRECT
		elif [ "$CORRECT" = "no" ] ; then
			printf "\033[31m%-7s\033[m  " $CORRECT
		else
			printf "\033[33m%-7s\033[m  " $CORRECT
		fi
		echo
	else
		if [ "$CORRECT" = "no" ] ; then
			CORRECT_SUM="no"
		fi
	fi
done
}

if [ "$MODE" = "single" ] ; then
	printf "%-60s%-7s" "FILE" "CORRECT"
	echo
	for i; do
		do_one $1
		shift
	done
	exit 0
fi

packages=`rpm -qa --queryformat "%{NAME}.%{ARCH}\n" | sort`
printf "%-50s  %-7s" "PACKAGE" "CORRECT"
echo
for p in $packages
do
	CORRECT_SUM="yes"
	printf "%-50s  " $p
	do_one $p
	if [ "$CORRECT_SUM" = "yes" ] ; then
		printf "\033[32m%-7s\033[m  " $CORRECT_SUM
	else
		printf "\033[31m%-7s\033[m  " $CORRECT_SUM
	fi
	echo
done
exit 0


