require xorg-app-common.inc

SUMMARY = "X Window System initializer"

DESCRIPTION = "The xinit program is used to start the X Window System \
server and a first client program on systems that cannot start X \
directly from /etc/init or in environments that use multiple window \
systems. When this first client exits, xinit will kill the X server and \
then terminate."

LIC_FILES_CHKSUM = "file://COPYING;md5=18f01e7b39807bebe2b8df101a039b68"

PE = "1"

SRC_URI[md5sum] = "4e928452dfaf73851413a2d8b8c76388"
SRC_URI[sha256sum] = "75d88d7397a07e01db253163b7c7a00b249b3d30e99489f2734cac9a0c7902b3"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie"

RDEPENDS_${PN} += "util-linux-mcookie"
