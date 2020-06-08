SUMMARY = "A library to handle Apple Property List format whereas it's binary or XML"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.LESSER;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "libxml2 glib-2.0 swig python3"

inherit autotools pkgconfig python3native

SRCREV = "3df02d4d0e9008771e8622fdc10de8333b3f0d85"
SRC_URI = "git://github.com/libimobiledevice/libplist;protocol=https \
           "

S = "${WORKDIR}/git"

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


