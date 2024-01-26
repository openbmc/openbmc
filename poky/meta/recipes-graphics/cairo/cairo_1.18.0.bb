SUMMARY = "The Cairo 2D vector graphics library"
DESCRIPTION = "Cairo is a multi-platform library providing anti-aliased \
vector-based rendering for multiple target backends. Paths consist \
of line segments and cubic splines and can be rendered at any width \
with various join and cap styles. All colors may be specified with \
optional translucence (opacity/alpha) and combined using the \
extended Porter/Duff compositing algebra as found in the X Render \
Extension."
HOMEPAGE = "http://cairographics.org"
BUGTRACKER = "https://gitlab.freedesktop.org/cairo/cairo/-/issues"
SECTION = "libs"

LICENSE = "(MPL-1.1 | LGPL-2.1-only) & GPL-3.0-or-later"
LICENSE:${PN} = "MPL-1.1 | LGPL-2.1-only"
LICENSE:${PN}-dev = "MPL-1.1 | LGPL-2.1-only"
LICENSE:${PN}-doc = "MPL-1.1 | LGPL-2.1-only"
LICENSE:${PN}-gobject = "MPL-1.1 | LGPL-2.1-only"
LICENSE:${PN}-script-interpreter = "MPL-1.1 | LGPL-2.1-only"
LICENSE:${PN}-perf-utils = "GPL-3.0-or-later"
# Adapt the licenses for cairo-dbg and cairo-src depending on whether
# cairo-trace is being built.
LICENSE:${PN}-dbg = "(MPL-1.1 | LGPL-2.1-only)${@bb.utils.contains('PACKAGECONFIG', 'trace', ' & GPL-3.0-or-later', '', d)}"
LICENSE:${PN}-src = "(MPL-1.1 | LGPL-2.1-only)${@bb.utils.contains('PACKAGECONFIG', 'trace', ' & GPL-3.0-or-later', '', d)}"

LIC_FILES_CHKSUM = "file://COPYING;md5=e73e999e0c72b5ac9012424fa157ad77 \
                    ${@bb.utils.contains('PACKAGECONFIG', 'trace', 'file://util/cairo-trace/COPYING-GPL-3;md5=d32239bcb673463ab874e80d47fae504', '', d)}"


DEPENDS = "fontconfig freetype glib-2.0 libpng pixman zlib"

SRC_URI = "http://cairographics.org/releases/cairo-${PV}.tar.xz \
           file://cairo-get_bitmap_surface-bsc1036789-CVE-2017-7475.diff \
          "

SRC_URI[sha256sum] = "243a0736b978a33dee29f9cca7521733b78a65b5418206fef7bd1c3d4cf10b64"

inherit meson pkgconfig upstream-version-is-even gtk-doc multilib_script

# if qemu usermode isn't available, this value needs to be set statically
# (otherwise it's determinted by running a small target executable with qemu)
do_write_config:append() {
    cat >${WORKDIR}/cairo.cross <<EOF
[properties]
ipc_rmid_deferred_release = 'true'
EOF
}
EXTRA_OEMESON:append:class-nativesdk = "${@' --cross-file ${WORKDIR}/cairo.cross' if d.getVar('EXEWRAPPER_ENABLED') == 'False' else ''}"
EXTRA_OEMESON:append:class-target = "${@' --cross-file ${WORKDIR}/cairo.cross' if d.getVar('EXEWRAPPER_ENABLED') == 'False' else ''}"

GTKDOC_MESON_OPTION = "gtk_doc"

MULTILIB_SCRIPTS = "${PN}-perf-utils:${bindir}/cairo-trace"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xlib xcb', '', d)} trace"
PACKAGECONFIG[xlib] = "-Dxlib=enabled,-Dxlib=disabled,virtual/libx11 libxrender libxext"
PACKAGECONFIG[xcb] = "-Dxcb=enabled,-Dxcb=disabled,libxcb"
# cairo-trace is GPLv3 so add an option to remove it
PACKAGECONFIG[trace] = ""

do_install:append () {
    if ! ${@bb.utils.contains('PACKAGECONFIG', 'trace', 'true', 'false', d)}; then
        rm ${D}${bindir}/cairo-trace ${D}${libdir}/cairo/libcairo-trace.so
        rmdir --ignore-fail-on-non-empty ${D}${bindir} ${D}${libdir}/cairo
    fi
}

PACKAGES =+ "cairo-gobject cairo-script-interpreter cairo-perf-utils"

SUMMARY:cairo-gobject = "The Cairo library GObject wrapper library"
DESCRIPTION:cairo-gobject = "A GObject wrapper library for the Cairo API."

SUMMARY:cairo-script-interpreter = "The Cairo library script interpreter"
DESCRIPTION:cairo-script-interpreter = "The Cairo script interpreter implements \
CairoScript.  CairoScript is used by tracing utilities to enable the ability \
to replay rendering."

DESCRIPTION:cairo-perf-utils = "The Cairo library performance utilities"

FILES:${PN} = "${libdir}/libcairo.so.*"
FILES:${PN}-gobject = "${libdir}/libcairo-gobject.so.*"
FILES:${PN}-script-interpreter = "${libdir}/libcairo-script-interpreter.so.*"
FILES:${PN}-perf-utils = "${bindir}/cairo-* ${libdir}/cairo/libcairo-trace.so ${libdir}/cairo/libcairo-fdr.so"

BBCLASSEXTEND = "native nativesdk"

UPSTREAM_CHECK_REGEX = "cairo-(?P<pver>\d+(\.\d+)+).tar.xz"
