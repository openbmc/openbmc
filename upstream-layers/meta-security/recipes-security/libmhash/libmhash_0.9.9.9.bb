SUMMARY = "Library of hashing algorithms."
DESCRIPTION = "\
  Mhash is a free (under GNU Lesser GPL) library \
  which provides a uniform interface to a large number of hash \
  algorithms. These algorithms can be used to compute checksums, \
  message digests, and other signatures. \
  "
HOMEPAGE = "http://mhash.sourceforge.net/"

LICENSE = "LGPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

S = "${UNPACKDIR}/mhash-${PV}"

SECTION = "libs"

SRC_URI = "${SOURCEFORGE_MIRROR}/mhash/mhash-${PV}.tar.bz2 \
    file://Makefile.test \
    file://mhash.c \
    file://run-ptest \
    "

SRC_URI[md5sum] = "f91c74f9ccab2b574a98be5bc31eb280"
SRC_URI[sha256sum] = "56521c52a9033779154432d0ae47ad7198914785265e1f570cee21ab248dfef0"

inherit autotools-brokensep ptest multilib_header

do_install:append() {
    oe_multilib_header mutils/mhash_config.h
}

do_compile_ptest() {
    if [ ! -d ${S}/demo ]; then mkdir ${S}/demo; fi
    cp ${UNPACKDIR}/Makefile.test ${S}/demo/Makefile
    cp ${UNPACKDIR}/mhash.c ${S}/demo/
    make -C ${S}/demo CFLAGS="${CFLAGS} -I${S}/include/" LDFLAGS="${LDFLAGS} -L${S}/lib/.libs"
}

do_install_ptest() {
    install -m 0755 ${S}/demo/mhash ${D}${PTEST_PATH}
}

BBCLASSEXTEND = "native"
