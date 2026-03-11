#!/bin/sh

# This software may be freely redistributed under the terms of the GNU
# public license.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

find /dev -context *:device_t:* \( -type c -o -type b \) -printf "%p %Z\n"


