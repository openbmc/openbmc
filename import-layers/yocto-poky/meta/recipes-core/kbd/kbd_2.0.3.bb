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
           file://run-ptest \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'file://set-proper-path-of-resources.patch', '', d)} \
          "

SRC_URI[md5sum] = "231b46e7142eb41ea3ae06d2ded3c208"
SRC_URI[sha256sum] = "7a899de1c0eb75f3aea737095a736f2375e1cbfbe693fc14a3fe0bfb4649fb5e"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
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
