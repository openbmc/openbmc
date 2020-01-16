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

SRC_URI[gphoto2.md5sum] = "0abd5ae8315109ae66bf06ac37902678"
SRC_URI[gphoto2.sha256sum] = "df87092100e7766c9d0a4323217c91908a9c891c0d3670ebf40b76903be458d1"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"

