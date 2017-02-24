DESCRIPTION = "Iperf is a tool to measure maximum TCP bandwidth, allowing the tuning of various parameters and UDP characteristics"
HOMEPAGE = "http://dast.nlanr.net/Projects/Iperf/"
SECTION = "console/network"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e8478eae9f479e39bc34975193360298"

SRC_URI = " ${SOURCEFORGE_MIRROR}/iperf/${BP}.tar.gz \
            file://iperf-2.0.5_ManPage.patch \
            file://0001-fix-out-of-tree-config.patch \
            file://0002-fix-bool-size-m4.patch \
          "

SRC_URI[md5sum] = "44b5536b67719f4250faed632a3cd016"
SRC_URI[sha256sum] = "636b4eff0431cea80667ea85a67ce4c68698760a9837e1e9d13096d20362265b"

S = "${WORKDIR}/${BP}"

inherit autotools pkgconfig

EXTRA_OECONF = "--exec-prefix=${STAGING_DIR_HOST}${layout_exec_prefix}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
