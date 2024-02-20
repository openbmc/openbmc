SUMMARY = "A coverage plugin to provide sensible default settings"
HOMEPAGE = "https://github.com/asottile/covdefaults"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3da826da635201a80d2fb40f3034929"

# Use GitHub SRC_URI, as pypi package does not include tests
SRC_URI += " \
    git://github.com/asottile/covdefaults.git;branch=main;protocol=https \
    file://run-ptest \
"

SRCREV = "007f5aff5d1c817883385a5f61f742dd11776dc6"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

RDEPENDS:${PN} += " \
    python3-coverage \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}
        cp -rf ${S}/tests ${D}${PTEST_PATH}
}
