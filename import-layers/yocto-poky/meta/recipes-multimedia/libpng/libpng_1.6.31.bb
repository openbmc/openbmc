SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f21e4e1f795810b5ea491b333bc6ea1 \
                    file://png.h;endline=144;md5=af300c419a45c53a8d2daa0b7d167a66"
DEPENDS = "zlib"

LIBV = "16"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/${BP}.tar.xz"
SRC_URI[md5sum] = "1b34eab440263e32cfa39d19413fad54"
SRC_URI[sha256sum] = "232a602de04916b2b5ce6f901829caf419519e6a16cc9cd7c1c91187d3ee8b41"

MIRRORS += "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/ ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/older-releases/${PV}/"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix ${bindir}/pngcp"

BBCLASSEXTEND = "native nativesdk"
