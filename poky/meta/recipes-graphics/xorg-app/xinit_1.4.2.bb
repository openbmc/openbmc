require xorg-app-common.inc

SUMMARY = "X Window System initializer"

DESCRIPTION = "The xinit program is used to start the X Window System \
server and a first client program on systems that cannot start X \
directly from /etc/init or in environments that use multiple window \
systems. When this first client exits, xinit will kill the X server and \
then terminate."

LIC_FILES_CHKSUM = "file://COPYING;md5=18f01e7b39807bebe2b8df101a039b68"

PE = "1"

SRC_URI += "file://0001-Make-manpage-multilib-identical.patch"

SRC_URI_EXT = "xz"

SRC_URI[sha256sum] = "b7d8dc8d22ef9f15985a10b606ee4f2aad6828befa437359934647e88d331f23"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie"

PACKAGECONFIG ??= "rxvt"
PACKAGECONFIG[rxvt] = "--with-xterm=rxvt,,,rxvt-unicode"

RDEPENDS:${PN} += "util-linux-mcookie"
