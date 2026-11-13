LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase upstream-version-is-even gobject-introspection

DEPENDS += " \
    glib-2.0 \
"
DEPENDS:append:libc-musl = " libucontext"

LDFLAGS:append:libc-musl = " -lucontext"

SRC_URI[archive.sha256sum] = "69f5b244c21f6402831c3ab8af85a01772f72fb118677388436f12f32bfb06d9"

PACKAGECONFIG ?= ""
PACKAGECONFIG[pygobject] = "-Dpygobject=true,-Dpygobject=false,,python3-pygobject"

EXTRA_OEMESON += "-Dintrospection=enabled -Dvapi=false"

FILES:${PN} += "${libdir}/python3*/site-packages/gi/overrides"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
