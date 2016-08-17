SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0b83047ce9e948b67c0facc5f233476a"
DEPENDS = "expat dbus dbus-glib virtual/libintl python-pyrex-native"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "b09cd2d1a057cc432ce944de3fc06bf7"
SRC_URI[sha256sum] = "e12c6c8b2bf3a9302f75166952cbe41d6b38c3441bbc6767dbd498942316c6df"
S = "${WORKDIR}/dbus-python-${PV}"

inherit distutils-base autotools pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[docs] = "--enable-html-docs,--disable-html-docs,python-docutils-native"
PACKAGECONFIG[api-docs] = "--enable-api-docs,--disable-api-docs,python-docutils-native python-epydoc-native"

export BUILD_SYS
export HOST_SYS

export STAGING_LIBDIR
export STAGING_INCDIR

RDEPENDS_${PN} = "python-io python-logging python-stringold python-threading python-xml"

FILES_${PN}-dev += "${libdir}/pkgconfig"
