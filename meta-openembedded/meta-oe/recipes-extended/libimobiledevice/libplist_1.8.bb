SUMMARY = "A library to handle Apple Property List format whereas it's binary or XML"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "libxml2 glib-2.0 swig python"

inherit cmake pkgconfig

SRC_URI = "http://www.libimobiledevice.org/downloads/libplist-${PV}.tar.bz2 \
           file://fix-parallel-make.patch \
           file://0001-Fix-warnings-found-with-clang.patch \
           "

SRC_URI[md5sum] = "2a9e0258847d50f9760dc3ece25f4dc6"
SRC_URI[sha256sum] = "a418da3880308199b74766deef2a760a9b169b81a868a6a9032f7614e20500ec"

do_install_append () {
    if [ -e ${D}${libdir}/python*/site-packages/plist/_plist.so ]; then
        chrpath -d ${D}${libdir}/python*/site-packages/plist/_plist.so
    fi
}

PACKAGES =+ "${PN}-utils ${PN}++ ${PN}-python"
FILES_${PN} = "${libdir}/libplist${SOLIBS}"
FILES_${PN}++ = "${libdir}/libplist++${SOLIBS}"
FILES_${PN}-utils = "${bindir}/*"
FILES_${PN}-python = "${libdir}/python*/site-packages/*"


