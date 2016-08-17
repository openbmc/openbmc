LICENSE = "GPLv2 | GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=be0de417af78dd340558810d1ced52e6"
SECTION = "x11/utils"
S = "${WORKDIR}/gnumeric-${PV}"
DEPENDS = "gdk-pixbuf libgsf gtk+3 libxml2 libglade libart-lgpl intltool-native libgnomecanvas libgnomeprint libbonoboui orbit2-native goffice"
SUMMARY = "Gnumeric spreadsheet for GNOME"

GNOME_COMPRESS_TYPE = "xz"

inherit gnome pythonnative gobject-introspection

SRC_URI += "file://do-not-use-srcdir.patch \
            file://0001-configure.in-drop-introspection-macros-replace-them-.patch"

SRC_URI[archive.md5sum] = "3fd87cca95334b5d8ac922989670fe27"
SRC_URI[archive.sha256sum] = "037b53d909e5d1454b2afda8c4fb1e7838e260343e36d4e36245f4a5d0e04111"


EXTRA_OECONF=" --without-perl "

PACKAGES_DYNAMIC += "gnumeric-plugin-*"
PACKAGES += "libspreadsheet libspreadsheet-dev gnumeric-goffice gnumeric-goffice-dbg"

FILES_${PN}-dbg += "${libdir}/gnumeric/${PV}/plugins/*/.debug"
FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES_${PN}-staticdev = "${libdir}/libspreadsheet.la"
FILES_libspreadsheet = "${libdir}/libspreadsheet-${PV}.so"
FILES_libspreadsheet-dev = "${libdir}/libspreadsheet.so"
FILES_gnumeric-goffice-dbg += "${libdir}/goffice/*/plugins/gnumeric/.debug"
FILES_gnumeric-goffice = "${libdir}/goffice/*/plugins/gnumeric/*"

# This hack works around the problem mentioned here:
# https://mail.gnome.org/archives/gnumeric-list/2010-February/msg00006.html
do_install_prepend() {
    sed -i ${B}/doc/C/Makefile -e 's/\tfor file in $(omffile); do/\t-for file in $(omffile); do/'
}

python populate_packages_prepend () {
    gnumeric_libdir = bb.data.expand('${libdir}/gnumeric/${PV}/plugins', d)

    do_split_packages(d, gnumeric_libdir, '^(.*)/.*$',
        output_pattern='gnumeric-plugin-%s',
        description='Gnumeric plugin %s',
        extra_depends='',
        recursive=True,
        prepend=True)
}
