SUMMARY = "Fast, pure-Python full text indexing, search, and spell checking library."
DESCRIPTION = "\
Whoosh is a fast, featureful full-text indexing and searching library \
implemented in pure Python. Programmers can use it to easily add search \
functionality to their applications and websites. Every part of how \
Whoosh works can be extended or replaced to meet your needs exactly."
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=05303186defc6141143629961c7c8a60"

SRC_URI += "file://0001-Mark-non-determinstic-test_minimize_dfa-test-as-XFAI.patch"

SRC_URI[md5sum] = "893433e9c0525ac043df33e6e04caab2"
SRC_URI[sha256sum] = "e0857375f63e9041e03fedd5b7541f97cf78917ac1b6b06c1fcc9b45375dda69"

PYPI_PACKAGE = "Whoosh"
PYPI_PACKAGE_EXT = "zip"

inherit ptest pypi setuptools3

RDEPENDS:${PN} += " \
    python3-email \
    python3-multiprocessing \
    python3-netclient \
    python3-numbers \
    python3-pickle \
    python3-shell \
    python3-stringold \
"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-fcntl \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
