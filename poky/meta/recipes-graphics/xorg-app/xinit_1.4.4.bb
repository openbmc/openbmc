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

SRC_URI[sha256sum] = "40a47c7a164c7f981ce3787b4b37f7e411fb43231dcde543d70094075dacfef9"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie \
                --with-xterm=x-terminal-emulator"

RDEPENDS:${PN} += "util-linux-mcookie"
RRECOMMENDS:${PN} += "virtual-x-terminal-emulator"
