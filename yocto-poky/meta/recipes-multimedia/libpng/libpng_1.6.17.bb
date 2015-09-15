SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b9b75399b72e4a8656cf3a6ddfc86d9a \
                    file://png.h;endline=16;md5=cc9c2d1eafda17e1277a6f99a9fc29c4 \
                    file://png.h;beginline=242;endline=356;md5=599316819d525dde2bfdf28fe3f323af"
DEPENDS = "zlib"
LIBV = "16"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/libpng/libpng${LIBV}/${PV}/libpng-${PV}.tar.xz \
          "
SRC_URI[md5sum] = "430a9b76b78533235cd4b9b26ce75c7e"
SRC_URI[sha256sum] = "98507b55fbe5cd43c51981f2924e4671fd81fe35d52dc53357e20f2c77fa5dfd"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix"

BBCLASSEXTEND = "native nativesdk"
