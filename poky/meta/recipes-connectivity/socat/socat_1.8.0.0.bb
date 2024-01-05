SUMMARY = "Multipurpose relay for bidirectional data transfer"
DESCRIPTION = "Socat is a relay for bidirectional data \
transfer between two independent data channels."
HOMEPAGE = "http://www.dest-unreach.org/socat/"

SECTION = "console/network"

LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://README;beginline=241;endline=271;md5=338c05eadd013872abb1d6e198e10a3f"

SRC_URI = "http://www.dest-unreach.org/socat/download/socat-${PV}.tar.bz2 \
           file://0001-fix-compile-procan.c-failed.patch \
"

SRC_URI[sha256sum] = "e1de683dd22ee0e3a6c6bbff269abe18ab0c9d7eb650204f125155b9005faca7"

inherit autotools

EXTRA_AUTORECONF += "--exclude=autoheader"

EXTRA_OECONF += "ac_cv_have_z_modifier=yes \
                 ac_cv_header_bsd_libutil_h=no \
                 sc_cv_termios_ispeed=no \
                 ${TERMBITS_SHIFTS} \
"

TERMBITS_SHIFTS ?= "sc_cv_sys_crdly_shift=9 \
                    sc_cv_sys_tabdly_shift=11 \
                    sc_cv_sys_csize_shift=4"

TERMBITS_SHIFTS:powerpc = "sc_cv_sys_crdly_shift=12 \
                           sc_cv_sys_tabdly_shift=10 \
                           sc_cv_sys_csize_shift=8"

TERMBITS_SHIFTS:powerpc64 = "sc_cv_sys_crdly_shift=12 \
                             sc_cv_sys_tabdly_shift=10 \
                             sc_cv_sys_csize_shift=8"

PACKAGECONFIG:class-target ??= "tcp-wrappers readline openssl"
PACKAGECONFIG ??= "readline openssl"
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"
PACKAGECONFIG[readline] = "--enable-readline,--disable-readline,readline"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"

CFLAGS += "-fcommon"

do_install:prepend () {
    mkdir -p ${D}${bindir}
    install -d ${D}${bindir} ${D}${mandir}/man1
}

BBCLASSEXTEND = "native nativesdk"
