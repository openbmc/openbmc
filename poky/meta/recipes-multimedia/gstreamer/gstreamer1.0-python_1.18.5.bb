SUMMARY = "Python bindings for GStreamer 1.0"
DESCRIPTION = "GStreamer Python binding overrides (complementing the bindings \
provided by python-gi) "
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[sha256sum] = "533685871305959d6db89507f3b3aa6c765c2f2b0dacdc32c5a6543e72e5bc52"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"
RDEPENDS:${PN} += "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"

PNREAL = "gst-python"

S = "${WORKDIR}/${PNREAL}-${PV}"

EXTRA_OEMESON += "-Dlibpython-dir=${libdir}"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

inherit meson pkgconfig distutils3-base upstream-version-is-even features_check

# Generating introspection data depends on a combination of native and target
# introspection tools, and qemu to run the target tools.
DEPENDS:append:class-target = " gobject-introspection gobject-introspection-native qemu-native prelink-native"

# Even though introspection is disabled on -native, gobject-introspection package is still
# needed for m4 macros.
DEPENDS:append:class-native = " gobject-introspection-native"
DEPENDS:append:class-nativesdk = " gobject-introspection-native"
