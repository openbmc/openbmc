SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4"
DEPENDS = "expat dbus glib-2.0 virtual/libintl"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz"

SRC_URI[sha256sum] = "92bdd1e68b45596c833307a5ff4b217ee6929a1502f5341bae28fd120acf7260"

S = "${WORKDIR}/dbus-python-${PV}"

inherit setuptools3-base autotools pkgconfig

# documentation needs python3-sphinx, which is not in oe-core or meta-python for now
# change to use PACKAGECONFIG when python3-sphinx is added to oe-core
EXTRA_OECONF += "--disable-documentation"


RDEPENDS:${PN} = "python3-io python3-logging python3-stringold python3-threading python3-xml"

FILES:${PN}-dev += "${libdir}/pkgconfig"

BBCLASSEXTEND = "native nativesdk"
