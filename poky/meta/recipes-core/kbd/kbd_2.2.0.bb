SUMMARY = "Keytable files and keyboard utilities"
HOMEPAGE = "http://www.kbd-project.org/"
# everything minus console-fonts is GPLv2+
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=892f569a555ba9c07a568a7c0c4fa63a"

inherit autotools gettext ptest pkgconfig

DEPENDS += "flex-native"

RREPLACES_${PN} = "console-tools"
RPROVIDES_${PN} = "console-tools"
RCONFLICTS_${PN} = "console-tools"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz \
           file://run-ptest \
           ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'file://set-proper-path-of-resources.patch', '', d)} \
           file://0001-analyze.l-add-missing-string-format.patch \
           file://0001-Use-DATADIR-and-append-i386-to-fix-libkbdfile-test08.patch \
           file://fix_cflags.patch \
           "

SRC_URI[md5sum] = "d1d7ae0b5fb875dc082731e09cd0c8bc"
SRC_URI[sha256sum] = "21a1bc5f6fb3b18ce9fdd717e4533368060a3182a39c7155eaf7ec0f5f83e9f7"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
                  ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests','', d)} \
                  "

PACKAGECONFIG[pam] = "--enable-vlock, --disable-vlock, libpam,"
PACKAGECONFIG[tests] = "--enable-tests, --disable-tests, libcheck"

do_compile_ptest() {
    oe_runmake -C ${B}/tests alt-is-meta dumpkeys-bkeymap dumpkeys-fulltable dumpkeys-mktable
}

do_install_ptest() {
    install -D ${B}/tests/Makefile ${D}${PTEST_PATH}/tests/Makefile
    sed -i -e '/Makefile:/,/^$/d' -e '/%: %.in/,/^$/d' \
	-e 's:--sysroot=${STAGING_DIR_TARGET}::g' \
	-e 's:${DEBUG_PREFIX_MAP}::g' \
	-e 's:${HOSTTOOLS_DIR}/::g' \
	-e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	-e 's:${RECIPE_SYSROOT}::g' \
	-e 's:${S}/config/missing::g' \
	-e 's:${WORKDIR}::g' \
	-e '/^lib.*_SOURCES =/d' -e '/$(EXEEXT):/,/^$/d' ${D}${PTEST_PATH}/tests/Makefile

    find ${B}/tests -executable -exec install {} ${D}${PTEST_PATH}/tests \;
    cp -rf ${S}/tests/data ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/findfile ${D}${PTEST_PATH}/tests
    cp -rf ${S}/data ${D}${PTEST_PATH}

    install -D -m 755 ${S}/config/test-driver ${D}${PTEST_PATH}/config/test-driver
}

PACKAGES += "${PN}-consolefonts ${PN}-keymaps ${PN}-unimaps ${PN}-consoletrans"

FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-keymaps = "${datadir}/keymaps"
FILES_${PN}-unimaps = "${datadir}/unimaps"

RDEPENDS_${PN}-ptest = "make"

inherit update-alternatives

ALTERNATIVE_${PN} = "chvt deallocvt fgconsole openvt showkey \
                     ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'vlock','', d)}"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native"
