DESCRIPTION = "Iperf is a tool to measure maximum TCP bandwidth, allowing the tuning of various parameters and UDP characteristics"
HOMEPAGE = "https://sourceforge.net/projects/iperf2/"
SECTION = "console/network"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e136a7b2560d80bcbf0d9b3e1356ecff"

SRC_URI = " ${SOURCEFORGE_MIRROR}/iperf2/iperf-${PV}.tar.gz"

SRC_URI[md5sum] = "097cf0754bc1afa165975c06a91e6906"
SRC_URI[sha256sum] = "7fe4348dcca313b74e0aa9c34a8ccd713b84a5615b8578f4aa94cedce9891ef2"

S = "${WORKDIR}/iperf-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF = "--exec-prefix=${STAGING_DIR_HOST}${layout_exec_prefix}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
