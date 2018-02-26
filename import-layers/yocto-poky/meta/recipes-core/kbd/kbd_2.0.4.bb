SUMMARY = "Keytable files and keyboard utilities"
HOMEPAGE = "http://www.kbd-project.org/"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5fcc36121d93e1f69d96a313078c8b5"
DEPENDS = "libcheck"

inherit autotools gettext ptest pkgconfig

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           file://run-ptest \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'file://set-proper-path-of-resources.patch', '', d)} \
          "

SRC_URI[md5sum] = "c1635a5a83b63aca7f97a3eab39ebaa6"
SRC_URI[sha256sum] = "5fd90af6beb225a9bb9b9fb414c090fba53c9a55793e172f508cd43652e59a88"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}"
PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"

do_compile_ptest() {
    oe_runmake -C ${B}/tests dumpkeys-fulltable alt-is-meta
}

do_install_ptest() {
    install -D ${B}/tests/Makefile ${D}${PTEST_PATH}/tests/Makefile
    sed -i -e '/Makefile:/,/^$/d' -e '/%: %.in/,/^$/d' \
	-e '/libkeymap_.*_SOURCES =/d' -e '/$(EXEEXT):/,/^$/d' ${D}${PTEST_PATH}/tests/Makefile

    find ${B}/tests -executable -exec install {} ${D}${PTEST_PATH}/tests \;
    find ${S}/tests \( -name \*.map -o -name \*.bin -o -name \*.output \) -exec install {} ${D}${PTEST_PATH}/tests \;

    install -D -m 755 ${S}/config/test-driver ${D}${PTEST_PATH}/config/test-driver
}

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

RDEPENDS_${PN}-ptest = "make"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt deallocvt fgconsole openvt"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
