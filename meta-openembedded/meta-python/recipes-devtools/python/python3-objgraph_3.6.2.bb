SUMMARY = "Draws Python object reference graphs with graphviz"
HOMEPAGE = "https://mg.pov.lt/objgraph/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e48a556235f55ad7d4234475657f68a7"

SRC_URI:append = " file://run-ptest"
SRC_URI[sha256sum] = "00b9f2f40f7422e3c7f45a61c4dafdaf81f03ff0649d6eaec866f01030e51ad8"

inherit pypi setuptools3 ptest-python-pytest

PACKAGECONFIG ??= ""
PACKAGECONFIG[ipython] = ",,,python3-graphviz"

do_install_ptest:append() {
	install -Dm 0644 ${S}/tests.py ${D}${PTEST_PATH}/tests.py
}

RDEPENDS:${PN} += "python3-core python3-io"
RDEPENDS:${PN}-ptest += "python3-tox"

PYPI_PACKAGE = "objgraph"
