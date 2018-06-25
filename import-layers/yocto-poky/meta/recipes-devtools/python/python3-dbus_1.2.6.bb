SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4"
DEPENDS = "expat dbus dbus-glib virtual/libintl"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

RSRC_URI[md5sum] = "1ce1ddf2582060f8f971652ea54cc17e"
SRC_URI[sha256sum] = "32f29c17172cdb9cb61c68b1f1a71dfe7351506fc830869029c47449bd04faeb"
S = "${WORKDIR}/dbus-python-${PV}"

inherit distutils3-base autotools pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[docs] = "--enable-html-docs,--disable-html-docs,python3-docutils-native"
PACKAGECONFIG[api-docs] = "--enable-api-docs,--disable-api-docs,python3-docutils-native python3-epydoc-native"

RDEPENDS_${PN} = "python3-io python3-logging python3-stringold python3-threading python3-xml"

FILES_${PN}-dev += "${libdir}/pkgconfig"
