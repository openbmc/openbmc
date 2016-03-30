SUMMARY = "Keytable files and keyboard utilities"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5fcc36121d93e1f69d96a313078c8b5"
DEPENDS = "libcheck"

inherit autotools gettext ptest pkgconfig

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           file://uclibc-stdarg.patch \
	   file://0003-Only-inluclude-kernel-headers-with-glibc.patch \
          "

SRC_URI[md5sum] = "87475eb78b1d6e6ab06686dd323ad4ba"
SRC_URI[sha256sum] = "9dfddabf96012e329c4bebb96a21aeef7c3872f624e96e8156ba542b82aeb912"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt deallocvt fgconsole openvt"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
PARALLEL_MAKEINST = ""
