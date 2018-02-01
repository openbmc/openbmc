SUMMARY = "The dump DAQ test the various inline mode features "
HOMEPAGE = "http://www.snort.org"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f9ce51a65dd738dc1ae631d8b21c40e0"

PARALLEL_MAKE = ""

DEPENDS = "libpcap libpcre libdnet"

SRC_URI = "http://fossies.org/linux/misc/daq-${PV}.tar.gz \
           file://disable-run-test-program-while-cross-compiling.patch \
           file://0001-correct-the-location-of-unistd.h.patch \
           "

# these 2 create undeclared dependency on libdnet and libnetfilter-queue from meta-networking
# this error from test-dependencies script:
# daq/daq/latest lost dependency on  libdnet libmnl libnetfilter-queue libnfnetlink
#
# never look to /usr/local lib while cross compiling

EXTRA_OECONF = "--disable-nfq-module --disable-ipq-module --includedir=${includedir} \
    --with-libpcap-includes=${STAGING_INCDIR} --with-dnet-includes=${STAGING_LIBDIR}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

SRC_URI[md5sum] = "2cd6da422a72c129c685fc4bb848c24c"
SRC_URI[sha256sum] = "b40e1d1273e08aaeaa86e69d4f28d535b7e53bdb3898adf539266b63137be7cb"

inherit autotools
