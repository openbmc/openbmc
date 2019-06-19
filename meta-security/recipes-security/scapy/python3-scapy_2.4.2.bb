inherit setuptools3
require python-scapy.inc

SRC_URI += "file://run-ptest"

do_install_append() {
        mv ${D}${bindir}/scapy ${D}${bindir}/scapy3
        mv ${D}${bindir}/UTscapy ${D}${bindir}/UTscapy3
}
