# Beecrypt OE build file
# Copyright (C) 2004-2005, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

SUMMARY = "A general-purpose cryptography library"
HOMEPAGE = "http://sourceforge.net/projects/beecrypt"

SRC_URI = "${SOURCEFORGE_MIRROR}/beecrypt/beecrypt-${PV}.tar.gz \
           file://disable-icu-check.patch \
           file://fix-security.patch \
           file://fix-for-gcc-4.7.patch \
           file://run-ptest \
           file://beecrypt-enable-ptest-support.patch \
           file://add-option-dev-dsp.patch \
          "

SRC_URI[md5sum] = "8441c014170823f2dff97e33df55af1e"
SRC_URI[sha256sum] = "286f1f56080d1a6b1d024003a5fa2158f4ff82cae0c6829d3c476a4b5898c55d"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=9894370afd5dfe7d02b8d14319e729a1 \
                    file://COPYING.LIB;md5=dcf3c825659e82539645da41a7908589 \
                    file://include/beecrypt/beecrypt.h;endline=20;md5=47a93eef539aac237eef86297a4d71c1"

PR = "r3"

inherit autotools multilib_header ptest
acpaths=""

do_install_append() {
	oe_multilib_header beecrypt/gnu.h
}

EXTRA_OECONF = "--without-python --enable-shared --enable-static --disable-openmp --with-java=no"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cplusplus] = "--with-cplusplus,--without-cplusplus,icu"

FILES_${PN} = "${sysconfdir} ${libdir}/*.so.* ${libdir}/${BPN}/*.so.*"
FILES_${PN}-dev += "${libdir}/${BPN}/*.so ${libdir}/${BPN}/*.la"
FILES_${PN}-staticdev += "${libdir}/${BPN}/*.a"

BBCLASSEXTEND = "native nativesdk"

do_install_ptest () {
	mkdir ${D}${PTEST_PATH}/tests
	cp -r ${B}/tests/.libs/test* ${D}${PTEST_PATH}/tests
}
