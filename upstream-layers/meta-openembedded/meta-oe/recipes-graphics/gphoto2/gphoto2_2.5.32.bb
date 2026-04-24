SUMMARY = "gphoto2 - a command-line frontend to libgphoto2"
HOMEPAGE = "http://www.gphoto.com/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "libgphoto2 popt readline"
RDEPENDS:gphoto2 = "libgphoto2"

SRC_URI = "${SOURCEFORGE_MIRROR}/gphoto/${BP}.tar.bz2;name=gphoto2 \
           file://0001-configure.ac-remove-AM_PO_SUBDIRS.patch \
           file://0001-configure-Filter-out-buildpaths-from-CC.patch \
           file://0001-gphoto2-fix-const-qualifier-violations.patch \
"
SRC_URI[gphoto2.sha256sum] = "4e379a0f12f72b49ee5ee2283ffd806b5d12d099939d75197a3f4bbc7f27a1a1"

inherit autotools pkgconfig gettext

EXTRA_OECONF += "--with-jpeg-prefix=${STAGING_INCDIR} \
                 --without-cdk \
"
