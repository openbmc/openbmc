SUMMARY = "Extends Python unittest to make testing easier"
DESCRIPTION = "nose extends the test loading and running features of unittest, \
making it easier to write, find and run tests."
SECTION = "devel/python"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://lgpl.txt;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI = "https://files.pythonhosted.org/packages/source/n/nose/nose-${PV}.tar.gz"

SRC_URI[md5sum] = "4d3ad0ff07b61373d2cefc89c5d0b20b"
SRC_URI[sha256sum] = "f1bffef9cbc82628f6e7d7b40d7e255aefaa1adb6a1b1d26c69a8b79e6208a98"

UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/nose/"
UPSTREAM_CHECK_REGEX = "/nose/(?P<pver>(\d+[\.\-_]*)+)"

S = "${WORKDIR}/nose-${PV}"

inherit setuptools3

do_install_append() {
    mv ${D}${bindir}/nosetests ${D}${bindir}/nosetests3
}

RDEPENDS_${PN}_class-target = "\
  python3-unittest \
  "

BBCLASSEXTEND = "native nativesdk"
