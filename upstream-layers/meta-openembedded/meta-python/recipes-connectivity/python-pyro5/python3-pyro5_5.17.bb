SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1c9ccd5f4ca5d0f5057c0e690a0153d"

SRC_URI[sha256sum] = "cfac69638d80943aff9cc5f1466755dd0fef8aed0bb4bda41b5eb045818ce6fc"

PYPI_PACKAGE = "pyro5"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-json \
    python3-logging \
    python3-serpent \
    python3-threading \
    "

RDEPENDS:${PN}-ptest += " \
    python3-html \
    python3-misc \
    python3-sqlite3 \
    "

do_install_ptest:append(){
    cp -r ${S}/certs ${D}${PTEST_PATH}/
}
