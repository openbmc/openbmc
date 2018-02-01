SUMMARY = "Gnome application programming libraries"
LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605"
SECTION = "x11/gnome/libs"

inherit gnome lib_package

PR = "r2"

SRC_URI[archive.md5sum] = "a4345e6087ae6195d65a4674ffdca559"
SRC_URI[archive.sha256sum] = "b2c63916866485793b87398266dd7778548c1734923c272a94d84ee011b6f7a4"
SRC_URI += "file://0001-libgnome-Makefile.am-allow-deprecated-symbols.patch"
GNOME_COMPRESS_TYPE="bz2"

DEPENDS += "libcanberra gconf-native gnome-vfs libbonobo esound intltool-native gnome-common-native"

EXTRA_OECONF += "--disable-gtk-doc"

do_configure_prepend() {
    sed -i -e s:docs::g ${S}/Makefile.am
    echo "EXTRA_DIST = version.xml" > gnome-doc-utils.make
    echo "EXTRA_DIST = version.xml" > gtk-doc.make
}

FILES_${PN} += "${libdir}/bonobo/servers ${libdir}/bonobo/monikers/*.so \
                ${datadir}/gnome-background-properties ${datadir}/pixmaps"
FILES_${PN}-dev += "${libdir}/bonobo/monikers/*.la"
FILES_${PN}-staticdev += "${libdir}/bonobo/monikers/*.a"

PACKAGES =+ "gnome-common-schemas"

FILES_gnome-common-schemas = "${datadir}/gnome-background-properties ${datadir}/pixmaps ${sysconfdir}"

RDEPENDS_${PN} = "gnome-common-schemas"

FILES_${PN}-dbg += "${libdir}/bonobo/monikers/.debug"
