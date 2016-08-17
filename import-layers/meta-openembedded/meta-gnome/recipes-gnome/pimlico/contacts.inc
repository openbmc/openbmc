DESCRIPTION = "Address-book application."
HOMEPAGE = "http://pimlico-project.org/contacts.html"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "GPLv2 & GPLv2+ & GPLv3+ "
SECTION = "x11"
DEPENDS = "glib-2.0 gtk+ evolution-data-server intltool-native"
RDEPENDS_${PN} = "libedata-book"

inherit autotools pkgconfig gconf

EXTRA_OECONF += "--disable-gnome-vfs"
EXTRA_OEMAKE += "GCONF_DISABLE_MAKEFILE_SCHEMA_INSTALL=1"

do_install_append () {
    install -d ${D}/${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/stock_contact.png ${D}/${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/stock_person.png ${D}/${datadir}/pixmaps
}

FILES_${PN} += "${datadir}/pixmaps/stock_contact.png \
        ${datadir}/pixmaps/stock_person.png \
        ${datadir}/icons/hicolor"

SRC_URI = "file://stock_contact.png \
           file://stock_person.png \
           file://contacts-conditionally-install-schema.patch \
"
