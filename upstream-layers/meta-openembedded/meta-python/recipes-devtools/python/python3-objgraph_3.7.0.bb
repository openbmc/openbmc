SUMMARY = "Draws Python object reference graphs with graphviz"
HOMEPAGE = "https://mg.pov.lt/objgraph/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e48a556235f55ad7d4234475657f68a7"

SRC_URI:append = " file://run-ptest \
                   file://0001-Make-test_at_addrs-independent-of-earlier-get_new_id.patch \
                   "
SRC_URI[sha256sum] = "b6ef4b244217410509b257970413b01a979d54df48d2d4055b6941e8679ddbbb"

inherit pypi setuptools3 ptest-python-pytest

PACKAGECONFIG ??= ""
PACKAGECONFIG[ipython] = ",,,python3-graphviz"

do_install_ptest:append() {
	install -Dm 0644 ${S}/tests.py ${D}${PTEST_PATH}/tests.py
}

RDEPENDS:${PN} += "python3-core python3-io"
RDEPENDS:${PN}-ptest += "python3-tox"
