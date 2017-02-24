SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0b83047ce9e948b67c0facc5f233476a"
DEPENDS = "expat dbus dbus-glib virtual/libintl"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "7372a588c83a7232b4e08159bfd48fe5"
SRC_URI[sha256sum] = "e2f1d6871f74fba23652e51d10873e54f71adab0525833c19bad9e99b1b2f9cc"
S = "${WORKDIR}/dbus-python-${PV}"

inherit distutils3-base autotools pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[docs] = "--enable-html-docs,--disable-html-docs,python3-docutils-native"
PACKAGECONFIG[api-docs] = "--enable-api-docs,--disable-api-docs,python3-docutils-native python3-epydoc-native"

RDEPENDS_${PN} = "python3-io python3-logging python3-stringold python3-threading python3-xml"

FILES_${PN}-dev += "${libdir}/pkgconfig"
