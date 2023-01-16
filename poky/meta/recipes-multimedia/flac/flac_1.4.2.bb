SUMMARY = "Free Lossless Audio Codec"
DESCRIPTION = "FLAC stands for Free Lossless Audio Codec, a lossless audio compression format."
HOMEPAGE = "https://xiph.org/flac/"
BUGTRACKER = "https://github.com/xiph/flac/issues"
SECTION = "libs"
LICENSE = "GFDL-1.2 & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.FDL;md5=ad1419ecc56e060eccf8184a87c4285f \
                    file://src/Makefile.am;beginline=1;endline=17;md5=146d2c8c2fd287545cc1bd81f31e8758 \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/flac/main.c;beginline=1;endline=18;md5=893456854ce6bf14a1a7ea77266eebab \
                    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING.Xiph;md5=3d6da238b5b57a0965d6730291119f65 \
                    file://include/FLAC/all.h;beginline=65;endline=70;md5=39aaf5e03c7364363884c8b8ddda8eea"

SRC_URI = "http://downloads.xiph.org/releases/flac/${BP}.tar.xz"
SRC_URI[sha256sum] = "e322d58a1f48d23d9dd38f432672865f6f79e73a6f9cc5a5f57fcaa83eb5a8e4"

CVE_PRODUCT = "libflac flac"

inherit autotools gettext

EXTRA_OECONF = "--disable-oggtest \
                --without-libiconv-prefix \
                ac_cv_prog_NASM="" \
                "

PACKAGECONFIG ??= " \
    ${@bb.utils.filter("TUNE_FEATURES", "altivec vsx", d)} \
    ogg \
"
PACKAGECONFIG[altivec] = "--enable-altivec,--disable-altivec"
PACKAGECONFIG[vsx] = "--enable-vsx,--disable-vsx"
PACKAGECONFIG[avx] = "--enable-avx,--disable-avx"
PACKAGECONFIG[ogg] = "--enable-ogg --with-ogg-libraries=${STAGING_LIBDIR} --with-ogg-includes=${STAGING_INCDIR},--disable-ogg,libogg"

PACKAGES += "libflac libflac++"
FILES:${PN} = "${bindir}/*"
FILES:libflac = "${libdir}/libFLAC.so.*"
FILES:libflac++ = "${libdir}/libFLAC++.so.*"
