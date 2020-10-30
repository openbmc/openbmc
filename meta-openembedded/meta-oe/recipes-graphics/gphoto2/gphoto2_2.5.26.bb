SUMMARY = "gphoto2 - a command-line frontend to libgphoto2"
HOMEPAGE = "http://www.gphoto.com/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "libgphoto2 popt readline"
RDEPENDS_gphoto2 = "libgphoto2"

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/gphoto2-${PV}.tar.bz2;name=gphoto2 \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
           file://0002-Look-for-popt-with-GP_CHECK_LIBRARY-function.patch \
"

SRC_URI[gphoto2.md5sum] = "f663b10bca639290fdb150b6ffe23e93"
SRC_URI[gphoto2.sha256sum] = "7653213b05329c1dc2779efea3eff00504e12011436587aedc9aaa1e8665ab2f"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"

