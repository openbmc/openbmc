#!/bin/sh
# find_sh4errors utility
# Copyright (c) 2004 Steve Grubb. ALL RIGHTS RESERVED.
# sgrubb@redhat.com
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# This script will search a directory and its subdirectories for every shell
# script. It then runs sh -n to see if bash can determine if there are obvious
# parsing errors. It does have a bug in that bash -n does not take into 
# account someone may program an unconditional exit and then include man page
# generation information. It also fails to notice the exec command. When you
# run across files that do either of the above, add it to the KNOWN_BAD list.

if [ $# -ge 2 ] ; then
	echo "Usage: find_sh4errors [directory]" 1>&2
	exit 1
fi
INTERPRETERS="wish wishx tclsh guile rep itkwish expect /etc/kde/kdm/Xsession /etc/X11/xdm/Xsession /usr/bin/festival perl hfssh"
SKIP_DIRS="/opt /home /root"
KNOWN_BAD="/usr/bin/kde-build /usr/bin/cvsversion samples/copifuncs/copi.sendifm1 bashdb bash_completion_test"
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
#echo "Locating executables..."
/usr/bin/find $DIR -type f -perm /0111 -print >> $tempfile 2>/dev/null
FOUND=0
#echo "Refining list to shell scripts..."
while read f
do
	# Get just the shell scripts
	testf=`echo $f | /usr/bin/file -n -f - | egrep 'ourne|POSIX shell'`
	if [ x"$testf" != x ] ; then
		echo $f >> $tempfile2
		FOUND=1
	fi
done < $tempfile
/bin/rm -f $tempfile
if [ $FOUND -eq 0 ] ; then
	# Nothing to report, just exit
#	echo "Examining shell scripts in $DIR"
#	echo "No problems found"
	/bin/rm -f $tempfile2
	exit 0
fi
#echo "Examining shell scripts in $DIR"
FOUND=0
while read i
do
	# First see if the script calls an interpreter
	SKIP=0
	for lang in $INTERPRETERS
	do
		if `/bin/cat "$i" 2>/dev/null | \
				grep "exec[ \t].*$lang" >/dev/null` ; then
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
		if `echo "$i" | /bin/grep "^\$d" >/dev/null`; then
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
		if `echo "$i" | /bin/grep "$bad" >/dev/null`; then
			SKIP=1
			break
		fi
	done

	if [ $SKIP -eq 1 ] ; then
		continue
	fi

	# Now examine them for correctness
	interp=`/usr/bin/head -n 1 "$i" | /bin/awk '{ print $1 }' | \
							/usr/bin/tr -d '#!'`
	if [ x"$interp" = "x" -o ! -x "$interp" ] ; then
		interp="/bin/sh"
	fi
	$interp -n "$i" 2>/dev/null
	if [ $? -ne 0 ] ; then
		printf "%-44s" "$i"
		rpm -qf --queryformat "%{NAME}-%{VERSION}" $i
		echo
		FOUND=1
	fi
done < $tempfile2
/bin/rm -f $tempfile2
if [ $FOUND -eq 0 ] ; then
        # Nothing to report, just exit
#        echo "No problems found"
	exit 0
fi
exit 1

