SUMMARY = "Free Lossless Audio Codec"
DESCRIPTION = "FLAC stands for Free Lossless Audio Codec, a lossless audio compression format."
HOMEPAGE = "https://xiph.org/flac/"
BUGTRACKER = "https://github.com/xiph/flac/issues"
SECTION = "libs"
LICENSE = "GFDL-1.3 & GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING.FDL;md5=802e79e394e372d01e863e3f4058cf40 \
                    file://src/Makefile.am;beginline=1;endline=17;md5=9c882153132df8f3a1cb1a8ca1f2350f \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/flac/main.c;beginline=1;endline=18;md5=1e826b5083ba1e028852fe7ceec6a8ad \
                    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://COPYING.Xiph;md5=78a131b2ea50675d245d280ccc34f8b6 \
                    file://include/FLAC/all.h;beginline=65;endline=70;md5=39aaf5e03c7364363884c8b8ddda8eea \
                    "

SRC_URI = "http://downloads.xiph.org/releases/flac/${BP}.tar.xz \
           file://0001-API-documentation-replace-modules.html-by-topics.htm.patch"

SRC_URI[sha256sum] = "f2c1c76592a82ffff8413ba3c4a1299b6c7ab06c734dee03fd88630485c2b920"

CVE_PRODUCT = "libflac flac"

inherit autotools gettext

EXTRA_OECONF = "--disable-oggtest \
                --without-libiconv-prefix \
                "

# /usr/src/debug/flac/1.5.0/src/libFLAC++/metadata.cpp:913:
# (.text+0x2032): undefined reference to `__stack_chk_fail_local'
LDFLAGS:append:libc-musl = " -lssp_nonshared"

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
