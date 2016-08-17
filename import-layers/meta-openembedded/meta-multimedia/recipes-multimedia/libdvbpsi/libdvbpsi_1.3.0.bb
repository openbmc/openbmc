DESCRIPTION = "libdvbpsi is a simple library designed for decoding \
and generation of MPEG TS and DVB PSI tables according to standards \
ISO/IEC 13818 and ITU-T H.222.0."
HOMEPAGE = "http://www.videolan.org/developers/libdvbpsi.html"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://download.videolan.org/pub/libdvbpsi/${PV}/libdvbpsi-${PV}.tar.bz2"
SRC_URI[md5sum] = "2b217039a1299000c39423441f77e76a"
SRC_URI[sha256sum] = "a2fed1d11980662f919bbd1f29e2462719e0f6227e1a531310bd5a706db0a1fe"

inherit autotools
