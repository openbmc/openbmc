SUMMARY = "Gstreamer validation tool"
DESCRIPTION = "A Tool to test GStreamer components"
HOMEPAGE = "https://gstreamer.freedesktop.org/documentation/gst-devtools/index.html"
SECTION = "multimedia"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://validate/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

#S = "${WORKDIR}/gst-devtools-${PV}"

SRC_URI = "https://gstreamer.freedesktop.org/src/gst-devtools/gst-devtools-${PV}.tar.xz \
           file://0001-connect-has-a-different-signature-on-musl.patch \
           "

SRC_URI[sha256sum] = "3025fee3607caf5069154c099533785675916e044ee92c936bbeacdda3750f26"

DEPENDS = "json-glib glib-2.0 glib-2.0-native gstreamer1.0 gstreamer1.0-plugins-base"
RRECOMMENDS_${PN} = "git"

FILES_${PN} += "${datadir}/gstreamer-1.0/* ${libdir}/gst-validate-launcher/* ${libdir}/gstreamer-1.0/*"

inherit meson pkgconfig gettext upstream-version-is-even gobject-introspection

# TODO: put this in a gettext.bbclass patch
def gettext_oemeson(d):
    if d.getVar('USE_NLS') == 'no':
        return '-Dnls=disabled'
    # Remove the NLS bits if USE_NLS is no or INHIBIT_DEFAULT_DEPS is set
    if d.getVar('INHIBIT_DEFAULT_DEPS') and not oe.utils.inherits(d, 'cross-canadian'):
        return '-Dnls=disabled'
    return '-Dnls=enabled'

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Ddebug_viewer=disabled \
    -Dtests=disabled \
    -Dvalidate=enabled \
    ${@gettext_oemeson(d)} \
"

do_install_append () {
     for fn in ${bindir}/gst-validate-launcher \
         ${libdir}/gst-validate-launcher/python/launcher/config.py; do
             sed -i -e 's,${B},/usr/src/debug/${PN},g' -e 's,${S},/usr/src/debug/${PN},g' ${D}$fn
     done
}

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"
