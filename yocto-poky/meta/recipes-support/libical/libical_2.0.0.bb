SUMMARY = "iCal and scheduling (RFC 2445, 2446, 2447) library"
HOMEPAGE = "https://github.com/libical/libical"
BUGTRACKER = "https://github.com/libical/libical/issues"
LICENSE = "LGPLv2.1 | MPL-1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d4fc58309d8ed46587ac63bb449d82f8 \
                    file://LICENSE;md5=d1a0891cd3e582b3e2ec8fe63badbbb6"
SECTION = "libs"

SRC_URI = "https://github.com/${BPN}/${BPN}/releases/download/v${PV}/${BP}.tar.gz \
           file://Remove-cmake-check-for-Perl.patch \
           "
DEPENDS = "icu"

SRC_URI[md5sum] = "6bf8e5f5a3ba88baf390d0134e05d76e"
SRC_URI[sha256sum] = "654c11f759c19237be39f6ad401d917e5a05f36f1736385ed958e60cf21456da"
UPSTREAM_CHECK_URI = "https://github.com/libical/libical/releases"

inherit cmake

FILES_${PN}-dev += "${libdir}/cmake/*"

#
# Turn specific library paths in the pkgconfig file into standard library entries
#
do_install_append () {
	sed -i -e "s#${STAGING_LIBDIR}/lib\([a-z0-9]*\)\.so#-l\1#g" ${D}${libdir}/pkgconfig/libical.pc
}
