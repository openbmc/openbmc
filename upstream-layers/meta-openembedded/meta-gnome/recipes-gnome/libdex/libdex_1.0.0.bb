LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

GNOMEBASEBUILDCLASS = "meson"
inherit features_check gnomebase upstream-version-is-even gobject-introspection

DEPENDS += " \
    glib-2.0 \
"
DEPENDS:append:libc-musl = " libucontext"

LDFLAGS:append:libc-musl = " -lucontext"

SRC_URI[archive.sha256sum] = "7b8f5c5db3796e14e12e10422e2356766ba830b92815fee70bbc867b5b207f5d"

PACKAGECONFIG ?= ""
EXTRA_OEMESON += "-Dintrospection=enabled -Dvapi=false"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
