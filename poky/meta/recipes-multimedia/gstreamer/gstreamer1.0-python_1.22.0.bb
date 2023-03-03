SUMMARY = "Python bindings for GStreamer 1.0"
DESCRIPTION = "GStreamer Python binding overrides (complementing the bindings \
provided by python-gi) "
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[sha256sum] = "6c63ad364ca4617eb2cbb3975ab26c66760eb3c7a6adf5be69f99c11e21ef3a5"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"
RDEPENDS:${PN} += "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"

PNREAL = "gst-python"

S = "${WORKDIR}/${PNREAL}-${PV}"

EXTRA_OEMESON += "\
    -Dtests=disabled \
    -Dplugin=enabled \
    -Dlibpython-dir=${libdir} \
"

inherit meson pkgconfig setuptools3-base upstream-version-is-even
