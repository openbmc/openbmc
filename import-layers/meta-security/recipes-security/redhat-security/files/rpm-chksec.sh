#!/bin/sh
# rpm-chksec
#
# Copyright (c) 2011-2013 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# Given an rpm, it will look at each file to check that its compiled with 
# the intended flags to make it more secure. Things that are green are OK.
# Anything in yellow could be better but is passable. Anything in red needs
# attention.
#
# If the --all option is given, it will generate a list of rpms and then
# summarize the rpm's state. For yes, then all files are in the expected
# state. Just one file not compiled with the right flags can turn the
# answer to no. Re-run passing that package (instead of --all) for the details.
#
# To save to file: ./rpm-chksec | sed -r "s/\x1B\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K]//g" | tee output.txt

VERSION="0.5.2"

usage () {
	echo "rpm-chksec [--version|--all|<rpmname>...]"
	if [ ! -x /usr/bin/filecap ] ; then
		echo "You need to install libcap-ng-utils to test capabilities"
	fi
	if [ $EUID != 0 ] ; then
		echo "You might need to be root to read some files"
	fi
	exit 0
}

if [ "$1" = "--help" -o $# -eq 0 ] ; then
	usage
fi
if [ "$1" = "--version" ] ; then
	echo "rpm-chksec $VERSION"
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

# Look for daemons, need this for later...
DAEMON=""
for f in $files
do
	if [ ! -f "$f" ] ; then
		continue
	fi
	if [ `echo "$f" | grep '\/etc\/rc.d\/init.d'` ] ; then
		n=`basename "$f"`
	        t=`which "$n" 2>/dev/null`
        	if [ x"$t" != "x" ] ; then
                	DAEMON="$DAEMON $t"
	                continue
        	fi
	        t=`which "$n"d 2>/dev/null`
        	if [ x"$t" != "x" ] ; then
                	DAEMON="$DAEMON $t"
	                continue
        	fi
	        t=`cat "$f" 2>/dev/null | grep 'bin' | grep 'exit 5' | grep -v '\$'`
        	if [ x"$t" != "x" ] ; then
                	DAEMON="$DAEMON $t"
	                continue
        	fi
		if [ "$MODE" = "single" ] ; then
        		echo "Can't find the executable in $f but daemon rules would apply"
		fi
	elif [ `echo "$f" | grep '\/lib\/systemd\/'` ] ; then
		t=`cat "$f" | grep -i '^ExecStart=' | tr '=' ' ' | awk '{ print $2 }'`
		if [ x"$t" != "x" ] ; then
                	DAEMON="$DAEMON $t"
	                continue
        	fi
	fi
done

# Prevent garbled output when doing --all.
skip_current=0

for f in $files
do
	if [ ! -f "$f" ] ; then
		continue
	fi
	# Some packages have files with ~ in them. This avoids it.
	if ! echo "$f" | grep '^/' >/dev/null ; then
		continue
	fi
	if [ ! -r "$f" ] && [ $EUID != 0 ] ; then
		if [ $MODE = "single" ] ; then
			echo "Please re-test $f as the root user"
		else
			# Don't print results.
			skip_current=1
			echo "Please re-test $1 as the root user"
		fi
		continue
	fi
	if ! file "$f" | grep -qw 'ELF'; then
		continue
	fi
	RELRO="no"
	if readelf -l "$f" 2>/dev/null | grep -q 'GNU_RELRO'; then
		RELRO="partial"
	fi
	if readelf -d "$f" 2>/dev/null | grep -q 'BIND_NOW'; then
		RELRO="full"
	fi
	PIE="no"
	if readelf -h "$f" 2>/dev/null | grep -q 'Type:[[:space:]]*DYN'; then
		PIE="DSO"
		if readelf -d "$f" 2>/dev/null | grep -q '(DEBUG)'; then
			PIE="yes"
		fi
	fi
	APP=""
	if [ x"$DAEMON" != "x" ] ; then
		for d in $DAEMON
		do
			if [ "$f" = "$d" ] ; then
				APP="daemon"
				break
			fi
		done
	fi
	if [ x"$APP" = "x" ] ; then
		# See if this is a library or a setuid app
		if [ `echo "$f" | grep '\/lib' | grep '\.so'` ] ; then
			APP="library"
		elif [ `find "$f" -perm -004000 -type f -print` ] ; then
			APP="setuid"
		elif [ `find "$f" -perm -002000 -type f -print` ] ; then
			APP="setgid"
		elif [ -x /usr/bin/filecap ] && [ `filecap "$f" 2> /dev/null | wc -w` -gt 0 ] ; then
			APP="setcap"
		else
			syms1=`/usr/bin/readelf -s "$f" 2>/dev/null | egrep ' connect@.*GLIBC| listen@.*GLIBC| accept@.*GLIBC|accept4@.*GLIBC'`
			syms2=`/usr/bin/readelf -s "$f" 2>/dev/null | egrep ' getaddrinfo@.*GLIBC| getnameinfo@.*GLIBC| getservent@.*GLIBC| getservbyname@.*GLIBC| getservbyport@.*GLIBC|gethostbyname@.*GLIBC| gethostbyname2@.*GLIBC|  gethostbyaddr@.*GLIBC|  gethostbyaddr2@.*GLIBC'`
			if [ x"$syms1" != "x" ] ; then
				if [ x"$syms2" != "x" ] ; then
					APP="network-ip"
				else
					APP="network-local"
				fi
			fi
		fi
	fi
	if [ x"$APP" = "x" ] ; then
		APP="exec"
	fi

	# OK, ready for the output
	if [ "$MODE" = "single" ] ; then
		printf "%-56s %-10s  " "$f" $APP
		if [ "$APP" = "daemon" -o "$APP" = "setuid" -o "$APP" = "setgid" -o "$APP" = "setcap" -o "$APP" = "network-ip" -o "$APP" = "network-local" ] ; then
			if [ "$RELRO" = "full" ] ; then
				 printf "\033[32m%-7s\033[m  " $RELRO
			elif [ "$RELRO" = "partial" ] ; then
				printf "\033[33m%-7s\033[m  " $RELRO
			else
				printf "\033[31m%-7s\033[m  " $RELRO
			fi
			if [ "$PIE" = "yes" ] ; then
				printf "\033[32m%-4s\033[m" $PIE
			else
				printf "\033[31m%-4s\033[m" $PIE
			fi
		elif [ "$APP" = "library" ] ; then
			if [ "$RELRO" = "full" -o "$RELRO" = "partial" ] ; then
				 printf "\033[32m%-7s\033[m  " $RELRO
			else
				printf "\033[31m%-7s\033[m  " $RELRO
			fi
			printf "\033[32m%-4s\033[m" $PIE
		else
			# $APP = exec - we want partial relro
			if [ "$RELRO" = "no" ] ; then
				printf "\033[31m%-7s\033[m  " $RELRO
			else
				printf "\033[32m%-7s\033[m  " $RELRO
			fi
			printf "\033[32m%-4s\033[m" $PIE
		fi
		echo
	else
		if [ "$APP" = "daemon" -o "$APP" = "setuid" -o "$APP" = "setgid" -o "$APP" = "setcap" -o "$APP" = "network-ip" -o "$APP" = "network-local" ] ; then
			if [ "$RELRO" = "no" ] ; then
				RELRO_SUM="no"
				APP_SUM="$APP"
			fi
			if [ "$PIE" = "no" ] ; then
				PIE_SUM="no"
				APP_SUM="$APP"
			fi
		elif [ "$APP" = "library" ] ; then
			if [ "$RELRO" = "no" ] ; then
				RELRO_SUM="no"
				APP_SUM="$APP"
			fi
		# $APP = exec - must have partial or full relro
		elif [ "$RELRO" = "no" ] ; then
			RELRO_SUM="no"
			APP_SUM="$APP"
		fi
	fi
done
}

if [ "$MODE" = "single" ] ; then
	printf "%-56s %-10s  %-7s  %-4s" "FILE" "TYPE" "RELRO" "PIE"
	echo
	for i; do
		f=$(basename $1)
		# Strip the .rpm extension, if present.
		do_one ${f%%.rpm}
		shift
	done
	exit 0
fi

# Skip the kernel as its special
packages=`rpm -qa | egrep -v 'kernel.|debuginfo.|.noarch|gpg-pubkey' | sort`
printf "%-50s  %-5s  %-4s  %-14s" "PACKAGE" "RELRO" "PIE" "CLASS"
echo
for p in $packages
do
	RELRO_SUM="yes"
	PIE_SUM="yes"
	APP_SUM=""
	printf "%-50s  " $p
	do_one $p
	if [[ $skip_current -eq 1 ]] ; then
		continue
	fi
	if [ "$RELRO_SUM" = "yes" ] ; then
		printf "\033[32m%-5s\033[m  " "$RELRO_SUM"
	else
		printf "\033[31m%-5s\033[m  " "$RELRO_SUM"
	fi
	if [ "$PIE_SUM" = "yes" ] ; then
		printf "\033[32m%-4s\033[m" "$PIE_SUM"
		if [ "$RELRO_SUM" = "no" ] ; then
			printf "  %-14s" "$APP_SUM"
		fi
	else
		if [ "$APP_SUM" = "network-local" ] ; then
			printf "\033[33m%-4s\033[m  %-14s" "$PIE_SUM" "$APP_SUM"
		else
			printf "\033[31m%-4s\033[m  %-14s" "$PIE_SUM" "$APP_SUM"
		fi
	fi
	echo
done
exit 0


