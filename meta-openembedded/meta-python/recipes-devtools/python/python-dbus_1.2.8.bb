SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT & AFL-2.1 | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4 \
		    file://dbus-gmain/COPYING;md5=05c3eacd50f69bb1d58afec460baad57 "
DEPENDS = "expat dbus dbus-glib virtual/libintl python-pyrex-native"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "7379db774c10904f27e7e2743d90fb43"
SRC_URI[sha256sum] = "abf12bbb765e300bf8e2a1b2f32f85949eab06998dbda127952c31cb63957b6f"
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
