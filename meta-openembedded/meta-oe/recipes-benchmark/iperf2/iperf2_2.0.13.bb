DESCRIPTION = "Iperf is a tool to measure maximum TCP bandwidth, allowing the tuning of various parameters and UDP characteristics"
HOMEPAGE = "https://sourceforge.net/projects/iperf2/"
SECTION = "console/network"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e136a7b2560d80bcbf0d9b3e1356ecff"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/iperf-${PV}.tar.gz \
           file://0001-Detect-bool-definition-considering-stdbool.h-being-p.patch \
"

SRC_URI[md5sum] = "31ea1c6d5cbf80b16ff3abe4288dad5e"
SRC_URI[sha256sum] = "c88adec966096a81136dda91b4bd19c27aae06df4d45a7f547a8e50d723778ad"

S = "${WORKDIR}/iperf-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF = "--exec-prefix=${STAGING_DIR_HOST}${layout_exec_prefix}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
