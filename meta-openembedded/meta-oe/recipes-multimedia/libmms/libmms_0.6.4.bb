SUMMARY = "MMS stream protocol library"
HOMEPAGE = "http://sourceforge.net/projects/libmms/"
SECTION = "libs/multimedia"

LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "${SOURCEFORGE_MIRROR}/project/${BPN}/${BPN}/${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "3c05e05aebcbfcc044d9e8c2d4646cd8359be39a3f0ba8ce4e72a9094bee704f"

inherit autotools pkgconfig

do_install:append() {
    # The GLib dependency was removed in libmms 0.6.3, but the
    # "Requires" was not removed from the pkg-config file.  Since we
    # don't have (and don't want) the RDEPENDS on GLib, we should
    # remove the "Requires" line.
    sed -i '/^Requires: glib-2\.0$/d' ${D}${libdir}/pkgconfig/libmms.pc
}
