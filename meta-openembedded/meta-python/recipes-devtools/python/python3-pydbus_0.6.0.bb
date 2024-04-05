DESCRIPTION = "Pythonic DBus library"
HOMEPAGE = "https://pypi.python.org/pypi/pydbus/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a916467b91076e631dd8edb7424769c7"

SRCREV = "f2e6355a88351e7d644ccb2b4d67b19305507312"
SRC_URI = " \
    git://github.com/LEW21/pydbus.git;protocol=https;branch=master \
    file://0001-make-direction-attribute-conforming-to-introspect.dt.patch \
    file://0002-Support-asynchronous-calls-58.patch \
    file://0003-Support-transformation-between-D-Bus-errors-and-exce.patch \
    file://run-ptest \
"

inherit ptest setuptools3

S = "${WORKDIR}/git"

RDEPENDS:${PN} = "python3-pygobject \
                  python3-io \
                  python3-logging"

RDEPENDS:${PN}-ptests = "bash"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
