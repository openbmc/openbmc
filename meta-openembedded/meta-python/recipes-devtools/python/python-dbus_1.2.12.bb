SUMMARY = "Python bindings for the DBus inter-process communication system"
SECTION = "devel/python"
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "MIT & AFL-2.1 | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b03240518994df6d8c974675675e5ca4 \
		    file://dbus-gmain/COPYING;md5=99fece6728a80737c8fd3e7c734c17c4 "
DEPENDS = "expat dbus dbus-glib virtual/libintl python-pyrex-native"

SRC_URI = "http://dbus.freedesktop.org/releases/dbus-python/dbus-python-${PV}.tar.gz \
"

SRC_URI[md5sum] = "428b7a9e7e2d154a7ceb3e13536283e4"
SRC_URI[sha256sum] = "cdd4de2c4f5e58f287b12013ed7b41dee81d503c8d0d2397c5bd2fb01badf260"
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
