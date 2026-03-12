SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ab458ad281b60e6f1b39b3feafbfc05"

SRC_URI[sha256sum] = "825e1a88e9d9fb481b8d2ca709c6195558b6eaa97c559ad3a9a20aa2d12815a3"

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
    python3-unittest-automake-output \
"

do_install_ptest() {
    cp -f ${S}/tests/test*.py ${D}${PTEST_PATH}/
}
