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

SRC_URI[md5sum] = "6d506ab2efc17a08e87778654e099d37"
SRC_URI[sha256sum] = "de9b8f617b68a70f6caf87da01fcf0ebd2b75690cdcba9c921d0ef54fa54abb9"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie"

PACKAGECONFIG ??= "rxvt"
PACKAGECONFIG[rxvt] = "--with-xterm=rxvt,,,rxvt-unicode"

RDEPENDS:${PN} += "util-linux-mcookie"
