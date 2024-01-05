SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ab458ad281b60e6f1b39b3feafbfc05"

SRC_URI[sha256sum] = "bd28bae37714b45d5894d77160a16e2be36b64a3b618c81168b3684676aa498b"

SRC_URI += " \
    file://run-ptest \
"

inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-logging \
    python3-numbers \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
"

do_install_ptest() {
    cp -f ${S}/tests/test*.py ${D}${PTEST_PATH}/
}
