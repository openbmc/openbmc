SUMMARY = "The dump DAQ test the various inline mode features "
HOMEPAGE = "http://www.snort.org"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f9ce51a65dd738dc1ae631d8b21c40e0"

PARALLEL_MAKE = ""

DEPENDS = "libpcap libpcre libdnet bison-native"

SRC_URI = "http://fossies.org/linux/misc/daq-${PV}.tar.gz \
           file://disable-run-test-program-while-cross-compiling.patch \
           file://0001-correct-the-location-of-unistd.h.patch \
           "
SRC_URI[sha256sum] = "bdc4e5a24d1ea492c39ee213a63c55466a2e8114b6a9abed609927ae13a7705e"
# these 2 create undeclared dependency on libdnet and libnetfilter-queue from meta-networking
# this error from test-dependencies script:
# daq/daq/latest lost dependency on  libdnet libmnl libnetfilter-queue libnfnetlink
#
# never look to /usr/local lib while cross compiling

EXTRA_OECONF = "--disable-nfq-module --disable-ipq-module --includedir=${includedir} \
    --with-libpcap-includes=${STAGING_INCDIR} --with-dnet-includes=${STAGING_LIBDIR}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

inherit autotools

DISABLE_STATIC = ""

BBCLASSEXTEND = "native"
