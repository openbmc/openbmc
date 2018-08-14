SUMMARY = "Network scanning and manipulation tool"
DESCRIPTION = "Scapy is a powerful interactive packet manipulation program. It is able to forge or decode packets of a wide number of protocols, send them on the wire, capture them, match requests and replies, and much more. It can easily handle most classical tasks like scanning, tracerouting, probing, unit tests, attacks or network discovery (it can replace hping, 85% of nmap, arpspoof, arp-sk, arping, tcpdump, tethereal, p0f, etc.). It also performs very well at a lot of other specific tasks that most other tools can't handle, like sending invalid frames, injecting your own 802.11 frames, combining technics (VLAN hopping+ARP cache poisoning, VOIP decoding on WEP encrypted channel, ...), etc."
SECTION = "security"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://bin/scapy;beginline=9;endline=13;md5=1d5249872cc54cd4ca3d3879262d0c69"

SRC_URI = "https://github.com/secdev/${BPN}/archive/v${PV}.tar.gz;downloadfilename=${BP}.tar.gz \
           file://run-ptest \
"

SRC_URI[md5sum] = "336d6832110efcf79ad30c9856ef5842"
SRC_URI[sha256sum] = "67642cf7b806e02daeddd588577588caebddc3426db7904e7999a0b0334a63b5"

inherit setuptools ptest

do_install_ptest() {
    install -m 0644 ${S}/test/regression.uts ${D}${PTEST_PATH}
    sed -i 's,@PTEST_PATH@,${PTEST_PATH},' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS_${PN} = "tcpdump python-subprocess python-compression python-netclient  \
                  python-netserver python-pydoc python-pkgutil python-shell \
                  python-threading python-numbers python-pycrypto"
