SUMMARY = "Keytable files and keyboard utilities"
HOMEPAGE = "http://www.kbd-project.org/"
DESCRIPTION = "The kbd project contains tools for managing Linux console (Linux console, virtual terminals, keyboard, etc.) – mainly, what they do is loading console fonts and keyboard maps."

# consolefonts and keymaps contain also some public domain and author notice licenses
LICENSE = "GPL-2.0-or-later & LGPL-2.0-or-later & GPL-3.0-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://data/keymaps/pine/en.map;beginline=2;endline=15;md5=20914a59c0546a7b77ebf959bc88ad5d \
"
LICENSE:${PN} = "GPL-2.0-or-later & LGPL-2.0-or-later"
LICENSE:${PN}-consolefonts = "GPL-2.0-or-later"
LICENSE:${PN}-consoletrans = "GPL-2.0-or-later"
LICENSE:${PN}-keymaps-pine = "GPL-3.0-or-later"
LICENSE:${PN}-keymaps = "GPL-2.0-or-later"
LICENSE:${PN}-unimaps = "GPL-2.0-or-later"

inherit autotools gettext pkgconfig

DEPENDS += "flex-native"

RREPLACES:${PN} = "console-tools"
RPROVIDES:${PN} = "console-tools"
RCONFLICTS:${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           "

SRC_URI[sha256sum] = "f167d899d92b56ccf12f6f49355173f93870a95f15d8aeebf5fdcd28a621aca8"

# 'gzip -n' is set due to https://github.com/legionus/kbd/issues/124
EXTRA_OECONF = "--disable-tests --enable-compress='gzip -n'"
PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                  "

PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

PACKAGES += "${PN}-consolefonts ${PN}-keymaps-pine ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES:${PN}-consolefonts = "${datadir}/consolefonts"
FILES:${PN}-consoletrans = "${datadir}/consoletrans"
FILES:${PN}-keymaps-pine = "${datadir}/keymaps/pine"
FILES:${PN}-keymaps = "${datadir}/keymaps"
FILES:${PN}-unimaps = "${datadir}/unimaps"

RRECOMMENDS:${PN}-keymaps = "${PN}-keymaps-pine"

do_install:append () {
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', 'no', d)}" = "yes" ] \
    && [ -f ${D}${sysconfdir}/pam.d/vlock ]; then
        mv -f ${D}${sysconfdir}/pam.d/vlock ${D}${sysconfdir}/pam.d/vlock.kbd
    fi
}

inherit update-alternatives

ALTERNATIVE:${PN} = "chvt deallocvt fgconsole openvt showkey \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'vlock','', d)}"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
