SUMMARY = "Network scanning and manipulation tool"
DESCRIPTION = "Scapy is a powerful interactive packet manipulation program. It is able to forge or decode packets of a wide number of protocols, send them on the wire, capture them, match requests and replies, and much more. It can easily handle most classical tasks like scanning, tracerouting, probing, unit tests, attacks or network discovery (it can replace hping, 85% of nmap, arpspoof, arp-sk, arping, tcpdump, tethereal, p0f, etc.). It also performs very well at a lot of other specific tasks that most other tools can't handle, like sending invalid frames, injecting your own 802.11 frames, combining technics (VLAN hopping+ARP cache poisoning, VOIP decoding on WEP encrypted channel, ...), etc."
SECTION = "security"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://bin/scapy;beginline=9;endline=13;md5=1d5249872cc54cd4ca3d3879262d0c69"

SRC_URI[md5sum] = "d7d3c4294f5a718e234775d38dbeb7ec"
SRC_URI[sha256sum] = "452f714f5c2eac6fd0a6146b1dbddfc24dd5f4103f3ed76227995a488cfb2b73"

inherit pypi ptest

do_install_ptest() {
    install -m 0644 ${S}/test/regression.uts ${D}${PTEST_PATH}
    sed -i 's,@PTEST_PATH@,${PTEST_PATH},' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS_${PN} = "tcpdump ${PYTHON_PN}-compression ${PYTHON_PN}-netclient  \
                  ${PYTHON_PN}-netserver ${PYTHON_PN}-pydoc ${PYTHON_PN}-pkgutil ${PYTHON_PN}-shell \
                  ${PYTHON_PN}-threading ${PYTHON_PN}-numbers ${PYTHON_PN}-pycrypto"
