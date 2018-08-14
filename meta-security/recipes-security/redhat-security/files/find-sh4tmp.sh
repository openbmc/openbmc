#!/bin/sh
# find_sh4tmp utility
# Copyright (c) 2005 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# This script will search a directory and its subdirectories for all shell
# scripts. It will then search for the use of the tmp directory. If it finds
# this is true, it will then try to determine if mktemp or something 
# reasonable was used and exclude it. It has a bug in that it does not handle
# rm -f /tmp/ or mkdir /tmp/ correctly. If you run across files that do that,
# add them to the KNOWN_BAD list to ignore them.

if [ $# -ge 2 ] ; then
	echo "Usage: find_sh4tmp [directory]" 1>&2
	exit 1
fi
INTERPRETERS="wish wishx tclsh guile rep itkwish expect /etc/kde/kdm/Xsession /etc/X11/xdm/Xsession /usr/bin/festival perl hfssh"
SKIP_DIRS="/opt /home /root /mnt /media /dev /proc /selinux /sys /usr/share/doc"
KNOWN_BAD="kopete_latexconvert.sh cvs2dist fixfiles mysqlbug build/scripts/package/mkspec py-compile rc.sysinit init.d/xfs diff-jars grub-install mailshar vncserver Xsession sysreport cross-build vpkg rcs-to-cvs debug_check_log cvs2vendor tmpwatch ps2epsi mkdumprd xdg-open xdg-mime xdg-email gzexe"
DIR="/"
if [ $# -eq 1 ] ; then
	if [ -d "$1" ] ; then
		DIR="$1"
	else
		echo "Option passed in was not a directory" 1>&2
		exit 1
	fi
fi
tempfile=`mktemp /tmp/sh4.XXXXXX`
tempfile2=`mktemp /tmp/sh4.XXXXXX`
if [ -z "$tempfile" -o -z "$tempfile2" ] ; then
        echo ; echo "Unable to create tempfiles...aborting." 1>&2 ; echo
        exit 1
fi
trap "rm -f $tempfile; rm -f $tempfile2; exit 2" 1 2 3 5 15

# Get executable files
#echo "Scanning shell scripts in $DIR..."
find $DIR -type f -perm /0111 -print >> $tempfile 2>/dev/null
FOUND=0
while read f
do
	# Get just the shell scripts
	testf=`echo $f | file -n -f - | egrep 'ourne|POSIX shell'`
	if [ x"$testf" != x ] ; then
# FIXME: need to do something to get rid of echo, rm, or mkdir "/tmp/"
		test_res=`cat $f 2>/dev/null | grep '\/tmp\/' | grep -v 'mktemp' | grep -v '^#'`
		if [ x"$test_res" = x ] ; then
			continue
		fi

		# Do further examination...
		# First see if the script calls an interpreter
		SKIP=0
		for lang in $INTERPRETERS
		do
			if `cat "$f" | grep "exec[ \t].*$lang" >/dev/null` ; then
				SKIP=1
				break
			fi
		done

		if [ $SKIP -eq 1 ] ; then
			continue
		fi

		# See if this is in a dir we want to ignore
		for d in $SKIP_DIRS
		do
			if `echo "$f" | grep "^\$d" >/dev/null`; then
				SKIP=1
				break
			fi
		done

		if [ $SKIP -eq 1 ] ; then
			continue
		fi

		# Don't do the known naughty files
		for bad in $KNOWN_BAD
		do
			if `echo "$f" | grep "$bad" >/dev/null`; then
				SKIP=1
				break
			fi
		done

		if [ $SKIP -eq 1 ] ; then
			continue
		fi

		# Well its a bad one...out with it
		printf "%-44s" $f
		rpm -qf --queryformat "%{NAME}-%{VERSION}" $f
		echo
		FOUND=1
	fi
done < $tempfile
rm -f $tempfile
if [ $FOUND -eq 0 ] ; then
	# Nothing to report, just exit
#	echo "No problems found" 
	rm -f $tempfile2
	exit 0
fi
exit 1


