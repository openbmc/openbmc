SUMMARY = "A library to handle Apple Property List format whereas it's binary or XML"
HOMEPAGE = "https://github.com/libimobiledevice/libplist"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "libxml2 glib-2.0 swig python3"

inherit autotools pkgconfig python3native python3targetconfig

SRCREV = "c5a30e9267068436a75b5d00fcbf95cb9c1f4dcd"
SRC_URI = "git://github.com/libimobiledevice/libplist;protocol=https"

S = "${WORKDIR}/git"

do_install_append () {
    if [ -e ${D}${libdir}/python*/site-packages/plist/_plist.so ]; then
        chrpath -d ${D}${libdir}/python*/site-packages/plist/_plist.so
    fi
}

PACKAGES =+ "${PN}-utils \
             ${PN}++ \
             ${PN}-python"

FILES_${PN} = "${libdir}/libplist-2.0${SOLIBS}"
FILES_${PN}++ = "${libdir}/libplist++-2.0${SOLIBS}"
FILES_${PN}-utils = "${bindir}/*"
FILES_${PN}-python = "${libdir}/python*/site-packages/*"
