SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=06a1b6fde6d93170bb72201c8000bf3d \
                    file://png.h;endline=112;md5=9a8b5f83e1e8046df672deab87f235be"
DEPENDS = "zlib"

SRC_URI = "${GENTOO_MIRROR}/libpng-${PV}.tar.xz \
          "
SRC_URI[md5sum] = "3bacb4728f6694a64ad9052769d6a4ce"
SRC_URI[sha256sum] = "6c8f1849eb9264219bf5d703601e5abe92a58651ecae927a03d1a1aa15ee2083"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix"

BBCLASSEXTEND = "native nativesdk"
