DESCRIPTION = "libdvbpsi is a simple library designed for decoding \
and generation of MPEG TS and DVB PSI tables according to standards \
ISO/IEC 13818 and ITU-T H.222.0."
HOMEPAGE = "http://www.videolan.org/developers/libdvbpsi.html"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://download.videolan.org/pub/libdvbpsi/${PV}/libdvbpsi-${PV}.tar.bz2"
SRC_URI[sha256sum] = "02b5998bcf289cdfbd8757bedd5987e681309b0a25b3ffe6cebae599f7a00112"

inherit autotools
