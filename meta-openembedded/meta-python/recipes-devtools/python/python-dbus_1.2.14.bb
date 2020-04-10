SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT & AFL-2.1 | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4 \
		    file://dbus-gmain/COPYING;md5=99fece6728a80737c8fd3e7c734c17c4 "
DEPENDS = "expat dbus dbus-glib virtual/libintl python-pyrex-native"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "c8739234fca9fba26368d1a337abe830"
SRC_URI[sha256sum] = "b10206ba3dd641e4e46411ab91471c88e0eec1749860e4285193ee68df84ac31"
S = "${WORKDIR}/dbus-python-${PV}"

inherit distutils-base autotools pkgconfig

# documentation needs python-sphinx, which is not in oe-core or meta-python for now
# change to use PACKAGECONFIG when python-sphinx is added to oe-core or meta-python
EXTRA_OECONF += "--disable-documentation"

export STAGING_LIBDIR
export STAGING_INCDIR

RDEPENDS_${PN} = "python-io python-logging python-stringold python-threading python-xml"

FILES_${PN}-dev += "${libdir}/pkgconfig"

do_install_append() {
    # Remove files that clash with python3-dbus; their content is same
    rm ${D}${includedir}/dbus-1.0/dbus/dbus-python.h ${D}${libdir}/pkgconfig/dbus-python.pc
}
