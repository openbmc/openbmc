SUMMARY = "Keytable files and keyboard utilities"
HOMEPAGE = "http://www.kbd-project.org/"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

inherit autotools gettext pkgconfig

DEPENDS += "flex-native"

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           "

SRC_URI[sha256sum] = "685056143cb8effd0a1d44b5c391eb50d80dcfd014b1a4d6e2650a28d61cb82a"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                  "

PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt deallocvt fgconsole openvt showkey \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'vlock','', d)}"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
