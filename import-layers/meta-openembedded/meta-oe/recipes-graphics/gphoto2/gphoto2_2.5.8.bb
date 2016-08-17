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

SRC_URI[gphoto2.md5sum] = "bae369aee6881e590c5c91bdbb11a5f8"
SRC_URI[gphoto2.sha256sum] = "a9abcd15d95f205318d17e3ac12af7ce523d2bc4943709d50b0a12c30cc5ee4d"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"

