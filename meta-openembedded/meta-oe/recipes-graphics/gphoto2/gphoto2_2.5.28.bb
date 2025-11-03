SUMMARY = "gphoto2 - a command-line frontend to libgphoto2"
HOMEPAGE = "http://www.gphoto.com/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "libgphoto2 popt readline"
RDEPENDS:gphoto2 = "libgphoto2"

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/gphoto2-${PV}.tar.bz2;name=gphoto2 \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
           file://0001-gphoto2-Use-pthread_t-abstract-type-for-thead-IDs.patch \
           file://0001-Match-prototypes-of-callbacks-with-libgphoto.patch \
"
SRC_URI[gphoto2.sha256sum] = "2a648dcdf12da19e208255df4ebed3e7d2a02f905be4165f2443c984cf887375"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"

do_configure:append() {
	sed -i -e 's#${RECIPE_SYSROOT}##g' ${B}/config.h
}
