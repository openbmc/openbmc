SUMMARY = "A GObject library to create cabinet files"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcab"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

SRC_URI = "\
    ${GNOME_MIRROR}/gcab/${PV}/gcab-${PV}.tar.xz \
    file://0001-gcab-enums.c.etemplate-include-basename-instead-of-f.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "2f0c9615577c4126909e251f9de0626c3ee7a152376c15b5544df10fc87e560b"

inherit gobject-introspection gtk-doc manpages meson ptest-gnome vala

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('USE_NLS', 'yes', 'nls', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[nls] = "-Dnls=true,-Dnls=false"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstalled_tests=true,-Dtests=false -Dinstalled_tests=false"

BBCLASSEXTEND = "native"
