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
SRCREV = "1de09fe85fe5c9d60ea5c6de130374e170b5bc28"
SRC_URI = "git://github.com/secdev/scapy.git;branch=master;protocol=https;tag=v${PV} \
           file://0001-python3-scapy-skip-ICMP-regression-tests-in-ptest.patch \
           file://0002-main-initialize-banner-for-the-default-Python-shell.patch \
           file://run-ptest"

UPSTREAM_CHECK_COMMITS = "1"

CVE_PRODUCT = "scapy"

inherit python_setuptools_build_meta ptest

do_install:append() {
        mv ${D}${bindir}/scapy ${D}${bindir}/scapy3
}

do_install_ptest() {
    install -d ${D}${libdir}/test/pcaps
    install -m 0644 ${S}/scapy/tools/UTscapy.py ${D}${PTEST_PATH}
    install -m 0644 ${S}/test/regression.uts ${D}${PTEST_PATH}
    install -m 0644 ${S}/test/pcaps/bad_rsn_parsing_overrides_ssid.pcap ${D}${libdir}/test/pcaps/
    install -m 0644 ${S}/test/pcaps/macos.pcapng.gz ${D}${libdir}/test/pcaps/
    sed -i 's,@PTEST_PATH@,${PTEST_PATH},' ${D}${PTEST_PATH}/run-ptest

    # note1: if ipv6 isn't enabled, skip the related test (add '-K ipv6' argument)
    # note2: to make this test work, your ISP also must support ipv6 - the test is trying
    #        to ping google.com through ipv6.
    if [ -z "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}" ]; then
        sed -i 's/UTscapy.py/UTscapy.py -K ipv6/g' ${D}${PTEST_PATH}/run-ptest
    fi
}

RDEPENDS:${PN} = "tcpdump python3-compression python3-cryptography python3-netclient  \
                  python3-netserver python3-pydoc python3-pkgutil python3-shell \
                  python3-threading python3-numbers python3-fcntl python3-logging \
                  python3-difflib"
RDEPENDS:${PN}-ptest += "python3-json python3-mock python3-multiprocessing \
                         iproute2 tshark"

FILES:${PN}-ptest += "${libdir}/test/"
