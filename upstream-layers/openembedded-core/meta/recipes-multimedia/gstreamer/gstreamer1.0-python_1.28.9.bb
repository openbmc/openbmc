SUMMARY = "Python bindings for GStreamer 1.0"
DESCRIPTION = "GStreamer Python binding overrides (complementing the bindings \
provided by python-gi) "
HOMEPAGE = "http://cgit.freedesktop.org/gstreamer/gst-python/"
SECTION = "multimedia"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c34deae4e395ca07e725ab0076a5f740"

SRC_URI = "https://gstreamer.freedesktop.org/src/${PNREAL}/${PNREAL}-${PV}.tar.xz"
SRC_URI[sha256sum] = "e6ad2658fad5b96d99af72ceec7e48995ff792fbda438d34486d189ce047718a"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject gstreamer1.0-plugins-bad"
RDEPENDS:${PN} += "gstreamer1.0 gstreamer1.0-plugins-base python3-pygobject"

PNREAL = "gst-python"

S = "${UNPACKDIR}/${PNREAL}-${PV}"

EXTRA_OEMESON += "\
    -Dtests=disabled \
    -Dplugin=enabled \
    -Dlibpython-dir=${libdir} \
"

inherit meson pkgconfig setuptools3-base upstream-version-is-even features_check gobject-introspection-data

FILES:${PN} += "${libdir}/gstreamer-1.0"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

# python3-pygobject is skipped without GI data, which also depends on
# qemu-usermode in MACHINE_FEATURES, so skip along with it.
python() {
    if not bb.utils.to_boolean(d.getVar("GI_DATA_ENABLED")):
        raise bb.parse.SkipRecipe("GI not available")
}
