SUMMARY = "A Python module to customize the process title"
DESCRIPTION = "The setproctitle module allows a process to change its \
title (as displayed by system tools such as ps, top or MacOS Activity \
Monitor)."
HOMEPAGE = "https://github.com/dvarrazzo/py-setproctitle"
BUGTRACKER = "https://github.com/dvarrazzo/py-setproctitle/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=86d2d41b5f4f023f43466f8cb7adebaa"

inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "3b40d32a3e1f04e94231ed6dfee0da9e43b4f9c6b5450d53e6dd7754c34e0c50"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += "\
    packagegroup-core-buildessential \
    procps-ps \
    python3-dev \
    python3-multiprocessing \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"

INSANE_SKIP:${PN}-ptest = "dev-deps"
