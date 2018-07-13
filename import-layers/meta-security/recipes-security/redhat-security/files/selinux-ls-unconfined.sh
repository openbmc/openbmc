#!/bin/sh

# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# This checks for unconfined apps running, initrc and inetd are signs
# of missing transitions.

pidof xinetd >/dev/null
if [ $? -eq 0 ] ; then
ps -eZ | egrep "initrc|inetd" | egrep -v `pidof xinetd` | tr ':' ' ' | awk '{ printf "%s %s\n", $3, $NF }'
else
ps -eZ | egrep "initrc" | tr ':' ' ' | awk '{ printf "%s %s\n", $3, $NF }'
fi

