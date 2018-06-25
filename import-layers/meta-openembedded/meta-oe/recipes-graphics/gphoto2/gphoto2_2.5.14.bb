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

SRC_URI[gphoto2.md5sum] = "6c6a21b5e879330cdd71ef92dce36399"
SRC_URI[gphoto2.sha256sum] = "9302d02fb472d4936988382b7277ccdc4edaf7ede56c490278912ffd0627699c"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"

