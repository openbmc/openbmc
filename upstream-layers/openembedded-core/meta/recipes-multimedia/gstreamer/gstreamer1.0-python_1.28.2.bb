SUMMARY = "Python bindings for GStreamer 1.0"
DESCRIPTION = "GStreamer Python binding overrides (complementing the bindings \
provided by python-gi) "
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[sha256sum] = "12fdd8e19af97d797a6b2c195228e6c9edc4cddfa68274912b78ef66068ad822"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject gstreamer1.0-plugins-bad"
RDEPENDS:${PN} += "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"

PNREAL = "gst-python"

S = "${UNPACKDIR}/${PNREAL}-${PV}"

EXTRA_OEMESON += "\
    -Dtests=disabled \
    -Dplugin=enabled \
    -Dlibpython-dir=${libdir} \
"

inherit meson pkgconfig setuptools3-base upstream-version-is-even features_check

FILES:${PN} += "${libdir}/gstreamer-1.0"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
