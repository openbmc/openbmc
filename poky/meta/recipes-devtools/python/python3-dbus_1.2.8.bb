SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4"
DEPENDS = "expat dbus dbus-glib virtual/libintl"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "7379db774c10904f27e7e2743d90fb43"
SRC_URI[sha256sum] = "abf12bbb765e300bf8e2a1b2f32f85949eab06998dbda127952c31cb63957b6f"
S = "${WORKDIR}/dbus-python-${PV}"

inherit distutils3-base autotools pkgconfig

# documentation needs python3-sphinx, which is not in oe-core or meta-python for now
# change to use PACKAGECONFIG when python3-sphinx is added to oe-core
EXTRA_OECONF += "--disable-documentation"


RDEPENDS_${PN} = "python3-io python3-logging python3-stringold python3-threading python3-xml"

FILES_${PN}-dev += "${libdir}/pkgconfig"
