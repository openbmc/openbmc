# FIXME: the LIC_FILES_CHKSUM values have been updated by 'devtool upgrade'.
# The following is the difference between the old and the new license text.
# Please update the LICENSE value if needed, and summarize the changes in
# the commit message via 'License-Update:' tag.
# (example: 'License-Update: copyright years updated.')
#
# The changes:
#
# --- COPYING
# +++ COPYING
# @@ -2,7 +2,8 @@
#  		       Version 2, June 1991
#  
#   Copyright (C) 1989, 1991 Free Software Foundation, Inc.
# -                       59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
# + 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
# +
#   Everyone is permitted to copy and distribute verbatim copies
#   of this license document, but changing it is not allowed.
#  
# 
#

SUMMARY = "Alsa OSS Compatibility Package"
SECTION = "libs/multimedia"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ed055b4eff93da784176a01582e6ec1a"
DEPENDS = "alsa-lib"

SRC_URI = "ftp://ftp.alsa-project.org/pub/oss-lib/alsa-oss-${PV}.tar.bz2 \
    file://libio.patch \
"
SRC_URI[md5sum] = "9ec4bb783fdce19032aace086d65d874"
SRC_URI[sha256sum] = "64adcef5927e848d2e024e64c4bf85b6f395964d9974ec61905ae4cb8d35d68e"

inherit autotools

LEAD_SONAME = "libaoss.so.0"

do_configure_prepend () {
    touch NEWS README AUTHORS ChangeLog
    sed -i "s/libaoss.so/${LEAD_SONAME}/" ${S}/alsa/aoss.in
}

# http://errors.yoctoproject.org/Errors/Details/186961/
EXCLUDE_FROM_WORLD_libc-musl = "1"
