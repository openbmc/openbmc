LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase upstream-version-is-even gobject-introspection

DEPENDS += " \
    glib-2.0 \
"
DEPENDS:append:libc-musl = " libucontext"

LDFLAGS:append:libc-musl = " -lucontext"

SRC_URI[archive.sha256sum] = "7472e88090db2b228307505edeba4483e872681340cb0404ae64ac3da5bed0a6"

PACKAGECONFIG ?= ""
EXTRA_OEMESON += "-Dintrospection=enabled -Dvapi=false"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
