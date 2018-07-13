#!/bin/sh

# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

found=0
list=`rpm -qa --queryformat "%{NAME}-%{VERSION}.%{ARCH}\n" | grep '^lib' | egrep -v '\-utils\-|\-bin\-|\-tools\-|\-client\-|libreoffice|\-plugin\-'`
for p in $list
do
	bin=`rpm -ql $p  | egrep '^/bin|^/sbin|^/usr/bin|^/usr/sbin' | grep -v '\-config'`
	if [ "x$bin" != "x" ]; then
		testf=`echo $bin | /usr/bin/file -n -f - 2>/dev/null | grep ELF`
		if [ x"$testf" != "x" ] ; then
			found=1
			echo "$p could be split into a utils package"
		fi
	fi
done

if [ $found = 0 ]; then
	echo "No problems found"
	exit 0
fi

exit 1


