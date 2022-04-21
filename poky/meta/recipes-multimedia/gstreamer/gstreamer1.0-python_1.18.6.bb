SUMMARY = "Python bindings for GStreamer 1.0"
DESCRIPTION = "GStreamer Python binding overrides (complementing the bindings \
provided by python-gi) "
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[sha256sum] = "bdc0ea22fbd7335ad9decc151561aacc53c51206a9735b81eac700ce5b0bbd4a"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"
RDEPENDS:${PN} += "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"

PNREAL = "gst-python"

S = "${WORKDIR}/${PNREAL}-${PV}"

EXTRA_OEMESON += "-Dlibpython-dir=${libdir}"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST:append = " introspection"

inherit meson pkgconfig distutils3-base upstream-version-is-even gobject-introspection features_check
