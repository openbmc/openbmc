SUMMARY = "Keytable files and keyboard utilities"
HOMEPAGE = "http://www.kbd-project.org/"
DESCRIPTION = "The kbd project contains tools for managing Linux console (Linux console, virtual terminals, keyboard, etc.) – mainly, what they do is loading console fonts and keyboard maps."
# everything minus console-fonts is GPL-2.0-or-later
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

inherit autotools gettext pkgconfig

DEPENDS += "flex-native"

RREPLACES:${PN} = "console-tools"
RPROVIDES:${PN} = "console-tools"
RCONFLICTS:${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           "

SRC_URI[sha256sum] = "55f0740458cfd3a84e775e50d7e8b92dc01846db1edad8e2411ccc293ece9b9f"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                  "

PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES:${PN}-consolefonts = "${datadir}/consolefonts"
FILES:${PN}-consoletrans = "${datadir}/consoletrans"
FILES:${PN}-keymaps = "${datadir}/keymaps"
FILES:${PN}-unimaps = "${datadir}/unimaps"

do_install:append () {
    if [ ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', '', d)} = yes ] \
    && [ -f ${D}${sysconfdir}/pam.d/vlock ]; then
        mv -f ${D}${sysconfdir}/pam.d/vlock ${D}${sysconfdir}/pam.d/vlock.kbd
    fi
}

inherit update-alternatives

ALTERNATIVE:${PN} = "chvt deallocvt fgconsole openvt showkey \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'vlock','', d)}"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
