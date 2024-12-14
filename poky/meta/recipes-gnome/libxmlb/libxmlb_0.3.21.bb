SUMMARY = "A library to help create and query binary XML blobs"
HOMEPAGE = "https://github.com/hughsie/libxmlb"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = " \
	git://github.com/hughsie/libxmlb.git;branch=main;protocol=https \
	file://0001-xb-selftest.c-hardcode-G_TEST_SRCDIR.patch \
	file://run-ptest \
"
SRCREV = "4cff6ece25f47b8fb740881387e50a00df922c57"
S = "${WORKDIR}/git"

DEPENDS = "glib-2.0 xz zstd"

inherit gobject-introspection gtk-doc meson ptest-gnome lib_package pkgconfig

PACKAGECONFIG ??= "${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)}"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false"

GTKDOC_MESON_OPTION = "gtkdoc"

FILES:${PN} += "${datadir}"

BBCLASSEXTEND = "native"
