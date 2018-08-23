SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2db717a05a20e36f2fa673265bd31568 \
                    file://png.h;endline=144;md5=3ac9fd250a8786ae581e34254bf79429"
DEPENDS = "zlib"

LIBV = "16"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/${BP}.tar.xz \
           file://CVE-2018-13785.patch \
"
SRC_URI[md5sum] = "c05b6ca7190a5e387b78657dbe5536b2"
SRC_URI[sha256sum] = "2f1e960d92ce3b3abd03d06dfec9637dfbd22febf107a536b44f7a47c60659f6"

MIRRORS += "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/${PV}/ ${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}${LIBV}/older-releases/${PV}/"

UPSTREAM_CHECK_URI = "http://libpng.org/pub/png/libpng.html"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix ${bindir}/pngcp"

BBCLASSEXTEND = "native nativesdk"
