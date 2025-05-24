SUMMARY = "A Python module to customize the process title"
DESCRIPTION = "The setproctitle module allows a process to change its \
title (as displayed by system tools such as ps, top or MacOS Activity \
Monitor)."
HOMEPAGE = "https://github.com/dvarrazzo/py-setproctitle"
BUGTRACKER = "https://github.com/dvarrazzo/py-setproctitle/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=86d2d41b5f4f023f43466f8cb7adebaa"

inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "1e6eaeaf8a734d428a95d8c104643b39af7d247d604f40a7bebcf3960a853c5e"

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
