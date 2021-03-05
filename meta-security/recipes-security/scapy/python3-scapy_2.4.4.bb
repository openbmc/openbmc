SUMMARY = "Network scanning and manipulation tool"
DESCRIPTION = "Scapy is a powerful interactive packet manipulation program. It is able to forge or decode packets of a wide number of protocols, send them on the wire, capture them, match requests and replies, and much more. It can easily handle most classical tasks like scanning, tracerouting, probing, unit tests, attacks or network discovery (it can replace hping, 85% of nmap, arpspoof, arp-sk, arping, tcpdump, tethereal, p0f, etc.). It also performs very well at a lot of other specific tasks that most other tools can't handle, like sending invalid frames, injecting your own 802.11 frames, combining technics (VLAN hopping+ARP cache poisoning, VOIP decoding on WEP encrypted channel, ...), etc."
SECTION = "security"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"

SRCREV = "95ba5b8504152a1f820bbe679ccf03668cb5118f"
SRC_URI = "git://github.com/secdev/scapy.git \
           file://run-ptest"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

do_install_append() {
        mv ${D}${bindir}/scapy ${D}${bindir}/scapy3
        mv ${D}${bindir}/UTscapy ${D}${bindir}/UTscapy3
}

do_install_ptest() {
    install -m 0644 ${S}/test/regression.uts ${D}${PTEST_PATH}
    sed -i 's,@PTEST_PATH@,${PTEST_PATH},' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS_${PN} = "tcpdump ${PYTHON_PN}-compression ${PYTHON_PN}-cryptography ${PYTHON_PN}-netclient  \
                  ${PYTHON_PN}-netserver ${PYTHON_PN}-pydoc ${PYTHON_PN}-pkgutil ${PYTHON_PN}-shell \
                  ${PYTHON_PN}-threading ${PYTHON_PN}-numbers ${PYTHON_PN}-pycrypto"
