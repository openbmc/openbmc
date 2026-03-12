DESCRIPTION = "Python-based Network Connectivity Management"
HOMEPAGE = "https://pypi.python.org/pypi/pyconnman/"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3 ptest

SRC_URI += "file://run-ptest"
SRC_URI[sha256sum] = "d3a63a039c82b08a1171b003eafa62c6f128aa4eaa1ce7a55a9401b48f9ad926"

RDEPENDS:${PN} = "connman python3-dbus python3-pprint python3-future"
RDEPENDS:${PN}-ptest += "python3-mock python3-unittest-automake-output"

do_install_ptest(){
    cp -r ${S}/tests ${D}${PTEST_PATH}/
}
