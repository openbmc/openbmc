require xorg-app-common.inc

SUMMARY = "X Window System initializer"

DESCRIPTION = "The xinit program is used to start the X Window System \
server and a first client program on systems that cannot start X \
directly from /etc/init or in environments that use multiple window \
systems. When this first client exits, xinit will kill the X server and \
then terminate."

LIC_FILES_CHKSUM = "file://COPYING;md5=18f01e7b39807bebe2b8df101a039b68"

PE = "1"

SRC_URI[md5sum] = "2da154b2f80ca9637b1a17b13af0880c"
SRC_URI[sha256sum] = "230835eef2f5978a1e1344928168119373f6df1d0a32c09515e545721ee582ef"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie"

RDEPENDS_${PN} += "util-linux-mcookie"
