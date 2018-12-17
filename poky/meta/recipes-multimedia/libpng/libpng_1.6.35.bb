SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4791381a76f106ac4699f6261c65ee29 \
                    file://png.h;endline=144;md5=090ba5769782e8a1663a45fab1c2b36c \
                    "
DEPENDS = "zlib"

LIBV = "16"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "678b7e696a62a193ed3503b04bf449d6"
SRC_URI[sha256sum] = "23912ec8c9584917ed9b09c5023465d71709dce089be503c7867fec68a93bcd7"

MIRRORS += "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/ ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/older-releases/${PV}/"

UPSTREAM_CHECK_URI = "http://libpng.org/pub/png/libpng.html"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix ${bindir}/pngcp"

BBCLASSEXTEND = "native nativesdk"
