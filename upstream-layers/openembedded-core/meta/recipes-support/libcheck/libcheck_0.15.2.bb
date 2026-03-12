SUMMARY  = "Check - unit testing framework for C code"
DESCRIPTION = "It features a simple interface for defining unit tests, \
putting little in the way of the developer. Tests are run in a separate \
address space, so both assertion failures and code errors that cause \
segmentation faults or other signals can be caught. Test results are \
reportable in the following: Subunit, TAP, XML, and a generic logging format."
HOMEPAGE = "https://libcheck.github.io/check/"
SECTION = "devel"
LICENSE  = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "${GITHUB_BASE_URI}/download/${PV}/check-${PV}.tar.gz \
           file://automake-output.patch \
           file://subunit.patch \
           file://0001-Fix-texinfo-errors-and-warnings.patch \
           file://run-ptest \
"

SRC_URI[sha256sum] = "a8de4e0bacfb4d76dd1c618ded263523b53b85d92a146d8835eb1a52932fa20a"

GITHUB_BASE_URI = "https://github.com/libcheck/check/releases/"

S = "${UNPACKDIR}/check-${PV}"

inherit cmake pkgconfig texinfo github-releases ptest

RREPLACES:${PN} = "check (<= 0.9.5)"

EXTRA_OECMAKE:append:class-target = " -DAWK_PATH=${bindir}/awk"
EXTRA_OECMAKE = "-DENABLE_SUBUNIT_EXT=OFF"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    install -m 0755 ${B}/tests/check_check ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/tests/check_check_export ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/tests/ex_output ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/tests/check_nofork ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/tests/check_nofork_teardown ${D}${PTEST_PATH}/tests/
    install -m 0755 ${B}/tests/check_set_max_msg_size ${D}${PTEST_PATH}/tests/

    install -m 0755 ${S}/tests/*.sh ${D}${PTEST_PATH}/tests/

    install -m 0755 ${S}/tests/test_output_strings ${D}${PTEST_PATH}/tests/

    if [ -f ${B}/tests/test_vars ]; then
        install -m 0644 ${B}/tests/test_vars ${D}${PTEST_PATH}/tests/
        sed -i \
            -e 's|if \[ x"[^"]*" != x"\." \];|if \[ x"${TARGET_DBGSRC_DIR}/tests" != x"." \];|g' \
            -e 's|SRCDIR="[^"]*"|SRCDIR="${TARGET_DBGSRC_DIR}/tests/"|g' \
            ${D}${PTEST_PATH}/tests/test_vars
    fi
}

do_install:append:class-native() {
    create_cmdline_shebang_wrapper ${D}${bindir}/checkmk
}

BBCLASSEXTEND = "native nativesdk"

PACKAGES =+ "checkmk"

FILES:checkmk = "${bindir}/checkmk"

RDEPENDS:checkmk = "gawk"
