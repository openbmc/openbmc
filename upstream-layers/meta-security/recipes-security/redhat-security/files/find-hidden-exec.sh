#!/bin/sh
#
#
# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
#
# This program looks for hidden executables

find / -name '.*' -type f -perm /00111 2>/dev/null

# Also need to find hidden dirs and see if anything below it is hidden
hidden_dirs=`find / -name '.*' -type d 2>/dev/null`
for d in $hidden_dirs
do
	find $d -name '.*' -type f -perm /00111 2>/dev/null
done

