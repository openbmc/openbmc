SUMMARY = "Free Lossless Audio Codec"
DESCRIPTION = "FLAC stands for Free Lossless Audio Codec, a lossless audio compression format."
HOMEPAGE = "https://xiph.org/flac/"
BUGTRACKER = "https://github.com/xiph/flac/issues"
SECTION = "libs"
LICENSE = "GFDL-1.2 & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.FDL;md5=ad1419ecc56e060eccf8184a87c4285f \
                    file://src/Makefile.am;beginline=1;endline=17;md5=b1dab2704be7f01bfbd9b7f6d5f000a9 \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/flac/main.c;beginline=1;endline=18;md5=23099119c034d894bd1bf7ef5bd22101 \
                    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING.Xiph;md5=0c90e41ab2fa7e69ca9391330d870221 \
                    file://include/FLAC/all.h;beginline=65;endline=70;md5=39aaf5e03c7364363884c8b8ddda8eea"

SRC_URI = "http://downloads.xiph.org/releases/flac/${BP}.tar.xz"
SRC_URI[sha256sum] = "6c58e69cd22348f441b861092b825e591d0b822e106de6eb0ee4d05d27205b70"

CVE_PRODUCT = "libflac flac"

inherit autotools gettext

EXTRA_OECONF = "--disable-oggtest \
                --without-libiconv-prefix \
                ac_cv_prog_NASM="" \
                "

PACKAGECONFIG ??= " \
    ogg \
"
PACKAGECONFIG[avx] = "--enable-avx,--disable-avx"
PACKAGECONFIG[ogg] = "--enable-ogg --with-ogg-libraries=${STAGING_LIBDIR} --with-ogg-includes=${STAGING_INCDIR},--disable-ogg,libogg"

PACKAGES += "libflac libflac++"
FILES:${PN} = "${bindir}/*"
FILES:libflac = "${libdir}/libFLAC.so.*"
FILES:libflac++ = "${libdir}/libFLAC++.so.*"

do_install:append() {
    # make the links in documentation relative to avoid buildpaths reproducibility problem
    sed -i "s#${S}/include#${includedir}#g" ${D}${docdir}/flac/FLAC.tag ${D}${docdir}/flac/api/*.html
    # there is also one root path without trailing slash
    sed -i "s#${S}#/#g" ${D}${docdir}/flac/api/*.html
}
