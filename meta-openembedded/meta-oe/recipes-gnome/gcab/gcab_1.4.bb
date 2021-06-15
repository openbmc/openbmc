SUMMARY = "A GObject library to create cabinet files"
HOMEPAGE = "https://gitlab.gnome.org/GNOME/gcab"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "glib-2.0"

SRC_URI = "\
    ${GNOME_MIRROR}/gcab/${PV}/gcab-${PV}.tar.xz \
    file://run-ptest \
"
SRC_URI[sha256sum] = "67a5fa9be6c923fbc9197de6332f36f69a33dadc9016a2b207859246711c048f"

inherit gobject-introspection gtk-doc manpages meson ptest-gnome vala

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('USE_NLS', 'yes', 'nls', '', d)} \
    ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
"
PACKAGECONFIG[manpages] = ""
PACKAGECONFIG[nls] = "-Dnls=true,-Dnls=false"
PACKAGECONFIG[tests] = "-Dtests=true -Dinstalled_tests=true,-Dtests=false -Dinstalled_tests=false"

BBCLASSEXTEND = "native"
