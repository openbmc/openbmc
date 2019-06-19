inherit setuptools
require python-scapy.inc

SRC_URI += "file://run-ptest"

RDEPENDS_${PN} += "${PYTHON_PN}-subprocess"

do_install_append() {
        mv ${D}${bindir}/scapy ${D}${bindir}/scapy2
        mv ${D}${bindir}/UTscapy ${D}${bindir}/UTscapy2
}
