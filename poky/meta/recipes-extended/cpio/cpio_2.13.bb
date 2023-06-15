SUMMARY = "GNU cpio is a program to manage archives of files"
DESCRIPTION = "GNU cpio is a tool for creating and extracting archives, or copying files from one place to \
another. It handles a number of cpio formats as well as reading and writing tar files."
HOMEPAGE = "http://www.gnu.org/software/cpio/"
SECTION = "base"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI = "${GNU_MIRROR}/cpio/cpio-${PV}.tar.gz \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           file://0002-src-global.c-Remove-superfluous-declaration-of-progr.patch \
           file://0001-obstack-Fix-a-clang-warning.patch \
           file://CVE-2021-38185.patch \
           file://0001-Use-__alignof__-with-clang.patch \
           file://0001-Wrong-CRC-with-ASCII-CRC-for-large-files.patch \
           file://0001-Fix-appending-to-archives-bigger-than-2G.patch \
           file://run-ptest \
           file://test.sh \
           "

SRC_URI[md5sum] = "389c5452d667c23b5eceb206f5000810"
SRC_URI[sha256sum] = "e87470d9c984317f658567c03bfefb6b0c829ff17dbf6b0de48d71a4c8f3db88"

inherit autotools gettext texinfo ptest

# Issue applies to use of cpio in SUSE/OBS, doesn't apply to us
CVE_CHECK_IGNORE += "CVE-2010-4226"

EXTRA_OECONF += "DEFAULT_RMT_DIR=${sbindir}"

do_install () {
    autotools_do_install
    if [ "${base_bindir}" != "${bindir}" ]; then
        install -d ${D}${base_bindir}/
        mv "${D}${bindir}/cpio" "${D}${base_bindir}/cpio"
        if [ "${sbindir}" != "${bindir}" ]; then
            rmdir ${D}${bindir}/
        fi
    fi

    # Avoid conflicts with the version from tar
    mv "${D}${mandir}/man8/rmt.8" "${D}${mandir}/man8/rmt-cpio.8"
}

do_compile_ptest() {
    oe_runmake -C ${B}/gnu/ check
    oe_runmake -C ${B}/lib/ check
    oe_runmake -C ${B}/rmt/ check
    oe_runmake -C ${B}/src/ check
    oe_runmake -C ${B}/tests/ genfile
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/
    sed -i "/abs_/d" ${B}/tests/atconfig
    install --mode=755 ${B}/tests/atconfig ${D}${PTEST_PATH}/tests/
    sed -i "s%${B}/tests:%%g" ${B}/tests/atlocal
    sed -i "s%${B}/src:%%g" ${B}/tests/atlocal
    install --mode=755 ${B}/tests/atlocal ${D}${PTEST_PATH}/tests/
    install --mode=755 ${B}/tests/genfile ${D}${PTEST_PATH}/tests/
    install --mode=755 ${S}/tests/testsuite ${D}${PTEST_PATH}/tests/
    install --mode=755 ${WORKDIR}/test.sh ${D}${PTEST_PATH}/test.sh
    sed -i "s#@PTEST_PATH@#${PTEST_PATH}#g" ${D}${PTEST_PATH}/test.sh
}

# ptest.bbclass currently chowns the ptest directory explicitly, so we need to
# change permission after that has happened so the ptest user can write a
# temporary directory.
do_install_ptest_base:append() {
    chgrp -R ptest ${D}${PTEST_PATH}/
    chmod -R g+w ${D}${PTEST_PATH}/
}

# The tests need to run as a non-root user, so pull in the ptest user
DEPENDS:append:class-target = "${@bb.utils.contains('PTEST_ENABLED', '1', ' ptest-runner', '', d)}"
PACKAGE_WRITE_DEPS += "ptest-runner"

RDEPENDS:${PN}-ptest += "ptest-runner"

PACKAGES =+ "${PN}-rmt"

FILES:${PN}-rmt = "${sbindir}/rmt*"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "cpio"
ALTERNATIVE:${PN}-rmt = "rmt"

ALTERNATIVE_LINK_NAME[cpio] = "${base_bindir}/cpio"

ALTERNATIVE_PRIORITY[rmt] = "50"
ALTERNATIVE_LINK_NAME[rmt] = "${sbindir}/rmt"

BBCLASSEXTEND = "native nativesdk"
