SUMMARY = "Network scanning and manipulation tool"
DESCRIPTION = "Scapy is a powerful interactive packet manipulation program. \
It is able to forge or decode packets of a wide number of protocols, send \
them on the wire, capture them, match requests and replies, and much more. \
It can easily handle most classical tasks like scanning, tracerouting, probing, \
unit tests, attacks or network discovery (it can replace hping, 85% of nmap, \
arpspoof, arp-sk, arping, tcpdump, tethereal, p0f, etc.). It also performs very \
well at a lot of other specific tasks that most other tools can't handle, like \
sending invalid frames, injecting your own 802.11 frames, combining technics \
(VLAN hopping+ARP cache poisoning, VOIP decoding on WEP encrypted channel, ...), etc."
SECTION = "security"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# If you want ptest support, use the git repo
# UTscapy does not exist in the pypi pkg
#
SRCREV = "0474c37bf1d147c969173d52ab3ac76d2404d981"
SRC_URI = "git://github.com/secdev/scapy.git;branch=master;protocol=https \
           file://run-ptest"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_COMMITS = "1"

inherit setuptools3 ptest

do_install:append() {
        mv ${D}${bindir}/scapy ${D}${bindir}/scapy3
}

do_install_ptest() {
    install -m 0644 ${S}/scapy/tools/UTscapy.py ${D}${PTEST_PATH}
    install -m 0644 ${S}/test/regression.uts ${D}${PTEST_PATH}
    sed -i 's,@PTEST_PATH@,${PTEST_PATH},' ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN} = "tcpdump ${PYTHON_PN}-compression ${PYTHON_PN}-cryptography ${PYTHON_PN}-netclient  \
                  ${PYTHON_PN}-netserver ${PYTHON_PN}-pydoc ${PYTHON_PN}-pkgutil ${PYTHON_PN}-shell \
                  ${PYTHON_PN}-threading ${PYTHON_PN}-numbers ${PYTHON_PN}-fcntl ${PYTHON_PN}-logging \
                  ${PYTHON_PN}-difflib"
