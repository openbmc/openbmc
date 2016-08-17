SUMMARY = "Gtk+Extra is a set of custom widget for plots and images"
HOMEPAGE = "http://gtkextra.sourceforge.net/"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

SRC_URI = "${SOURCEFORGE_MIRROR}/gtkextra/${BP}.tar.gz \
           file://remove-tutorial.patch \
"

SRC_URI[md5sum] = "486cea93666020f85f101ed8341baf41"
SRC_URI[sha256sum] = "9cab6c5d6b792eb828d17cec2b9c1baba2ef57f789a290464afab80b53969e65"

DEPENDS = "gtk+"

inherit autotools pkgconfig gobject-introspection

# | ./.libs/libgtkextra-x11-3.0.so: error: undefined reference to 'roundint'
PNBLACKLIST[gtkextra] ?= "BROKEN: Fails to build with gcc-5"
