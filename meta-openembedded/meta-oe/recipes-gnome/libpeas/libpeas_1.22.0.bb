SUMMARY = "libpeas is a gobject-based plugins engine"
HOMEPAGE = "https://wiki.gnome.org/Projects/Libpeas"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

DEPENDS = "gnome-common gtk+3 intltool-native"

inherit gnomebase gobject-introspection gtk-doc gtk-icon-cache distro_features_check

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " --enable-introspection --disable-introspection"

SRC_URI[archive.md5sum] = "a20dc55c3f88ad06da9491cfd7de7558"
SRC_URI[archive.sha256sum] = "5b2fc0f53962b25bca131a5ec0139e6fef8e254481b6e777975f7a1d2702a962"

PACKAGECONFIG[python3] = "--enable-python3,--disable-python3,python3-pygobject"

export GIR_EXTRA_LIBS_PATH = "${B}/libpeas/.libs"

PACKAGES =+ "${PN}-demo ${PN}-python3"
FILES_${PN}-demo = " \
    ${bindir}/peas-demo \
    ${libdir}/peas-demo \
"

RDEPENDS_${PN}-python3 = "python3-pygobject"
FILES_${PN}-python3 = "${libdir}/libpeas-1.0/loaders/libpython3loader.so"
