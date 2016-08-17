SUMMARY = "Fribidi library for bidirectional text"
SECTION = "libs"
PR = "r1"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"
BBCLASSEXTEND = "native"

PROVIDES = "libfribidi"

inherit autotools lib_package pkgconfig

CFLAGS_append = "  -DPAGE_SIZE=4096 "

SRC_URI = "http://fribidi.org/download/fribidi-${PV}.tar.gz"

SRC_URI[md5sum] = "626db17d2d99b43615ad9d12500f568a"
SRC_URI[sha256sum] = "49cf91586e48b52fe25872ff66c1da0dff0daac2593f9f300e2af12f44f64177"
