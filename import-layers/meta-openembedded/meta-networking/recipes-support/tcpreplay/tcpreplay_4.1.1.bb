SUMMARY = "Use previously captured traffic to test network devices"

HOMEPAGE = "http://tcpreplay.synfin.net/"
SECTION = "net"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=890b830b22fd632e9ffd996df20338f8"

SRC_URI = "http://prdownloads.sourceforge.net/tcpreplay/${PV}/tcpreplay-${PV}.tar.gz \
           "
SRC_URI[md5sum] = "80394c33fe697b53b69eac9bb0968ae9"
SRC_URI[sha256sum] = "61b916ef91049cad2a9ddc8de6f5e3e3cc5d9998dbb644dc91cf3a798497ffe4"

DEPENDS = "libpcap"

EXTRA_OECONF += "--with-libpcap=${STAGING_DIR_HOST}/usr"

inherit siteinfo autotools-brokensep

