SUMMARY = "A Python module to customize the process title"
DESCRIPTION = "The setproctitle module allows a process to change its \
title (as displayed by system tools such as ps, top or MacOS Activity \
Monitor)."
HOMEPAGE = "https://github.com/dvarrazzo/py-setproctitle"
BUGTRACKER = "https://github.com/dvarrazzo/py-setproctitle/issues"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a9efff04c3ae658687dd38e20398a20d"

inherit pypi setuptools3 ptest

SRC_URI[sha256sum] = "bc2bc917691c1537d5b9bca1468437176809c7e11e5694ca79a9ca12345dcb9e"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += "\
    libatomic-dev \
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
