SUMMARY = "This library aims to be a friendly, portable C implementation of the AV1 Image File Format"
HOMEPAGE = "https://github.com/AOMediaCodec/libavif"
SECTION = "libs"
# Most is the code is under BSD-2, but libyuv is under BSD-3, and iccjpeg is under IJG
LICENSE = "BSD-2-Clause & BSD-3-Clause & IJG"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51549db0941829faeedcc86efec2f4c0"

SRC_URI = "git://github.com/AOMediaCodec/libavif.git;protocol=https;branch=main;tag=v${PV};name=libavif"
SRC_URI += "git://github.com/kmurray/libargparse.git;protocol=https;nobranch=1;name=libargparse;subdir=${S}/ext/libargparse"
SRC_URI += "git://chromium.googlesource.com/libyuv/libyuv;protocol=https;nobranch=1;name=libyuv;subdir=${S}/ext/libyuv"
SRCREV_libavif = "6543b22b5bc706c53f038a16fe515f921556d9b3"
SRCREV_libargparse = "ee74d1b53bd680748af14e737378de57e2a0a954"
SRCREV_libyuv = "6067afde563c3946eebd94f146b3824ab7a97a9c"
SRCREV_FORMAT = "libavif"

DEPENDS = "nasm-native"

inherit cmake pkgconfig

EXTRA_OECMAKE += " \
    -DAVIF_BUILD_MAN_PAGES=OFF \
    -DAVIF_CODEC_RAV1E=OFF \
    -DAVIF_LIBYUV=LOCAL \
"

PACKAGECONFIG ?= "dav1d gdk-pixbuf"
PACKAGECONFIG[apps] = "-DAVIF_BUILD_APPS=ON -DAVIF_LIBXML2=SYSTEM,-DAVIF_BUILD_APPS=OFF -DAVIF_LIBXML2=OFF,zlib libjpeg-turbo libpng libwebp libxml2"
PACKAGECONFIG[gdk-pixbuf] = "-DAVIF_BUILD_GDK_PIXBUF=ON,-DAVIF_BUILD_GDK_PIXBUF=OFF,gdk-pixbuf"
PACKAGECONFIG[aom] = "-DAVIF_CODEC_AOM=SYSTEM,-DAVIF_CODEC_AOM=OFF,aom"
PACKAGECONFIG[dav1d] = "-DAVIF_CODEC_DAV1D=SYSTEM,-DAVIF_CODEC_DAV1D=OFF,dav1d"
PACKAGECONFIG[svt] = "-DAVIF_CODEC_SVT=SYSTEM,-DAVIF_CODEC_SVT=OFF,svt-av1"

FILES:${PN} += "${libdir}/gdk-pixbuf-2.0 ${datadir}"
