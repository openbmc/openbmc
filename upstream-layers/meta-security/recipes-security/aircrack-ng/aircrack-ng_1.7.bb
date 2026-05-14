SUMMARY = "Aircrack-ng is a set of tools for auditing wireless networks"
DESCRIPTION = "Aircrack-ng is an 802.11 WEP and WPA-PSK keys cracking program that can recover keys once enough data packets have been captured. It implements the standard FMS attack along with some optimizations like KoreK attacks, as well as the PTW attack, thus making the attack much faster compared to other WEP cracking tools."
SECTION = "security"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=1fbd81241fe252ec0f5658a521ab7dd8"

DEPENDS = "libnl openssl libpcap"

SRC_URI = "http://download.aircrack-ng.org/${BP}.tar.gz \
           file://0001-Remove-build-directory-reference.patch \
"
SRC_URI[sha256sum] = "05a704e3c8f7792a17315080a21214a4448fd2452c1b0dd5226a3a55f90b58c3"

inherit autotools-brokensep pkgconfig python3targetconfig

PACKAGECONFIG ??= "pcre sqlite3 experimental"

PACKAGECONFIG[pcre] = ",,libpcre"
PACKAGECONFIG[sqlite3] = "--with-sqlite3=${STAGING_DIR_HOST}${prefix},--with-sqlite3=no,sqlite3"
PACKAGECONFIG[experimental] = "--with-experimental=yes,--with-experimental=no"
PACKAGECONFIG[ext-scripts] = "--with-ext-scripts=yes,--with-ext-scripts=no,python3-setuptools-native,ethtool python3 python3-requests"

# pcap is theoretically optional, but is not unless the configure script is modified
EXTRA_OECONF = " \
    --with-libpcap-include=${STAGING_INCDIR}/pcap \
    ac_cv_prog_ETHTOOL=${bindir}/ethtool \
"

EXTRA_OEMAKE = "pkgpythondir=${PYTHON_SITEPACKAGES_DIR}"

do_install:append() {
    # Remove to avoid TMPDIR leakage
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/airdrop-ng-install_files.txt
    rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/airgraph-ng-install_files.txt
    rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/airdrop/__pycache__/
    rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/airgraphviz/__pycache__/

    # Fix up Python paths for target
    for f in airdrop-ng airgraph-ng airodump-join; do
        if [ -f "${D}${bindir}/$f" ]; then
            sed -i -e 's,${PYTHON},/usr/bin/env python3,g' ${D}${bindir}/$f
        fi
    done

}

FILES:${PN} += " \
    ${libdir}/*.so \
    ${datadir}/airgraph-ng/ \
    ${PYTHON_SITEPACKAGES_DIR} \
"
FILES_SOLIBSDEV = ""
INSANE_SKIP:${PN} += "dev-so"
