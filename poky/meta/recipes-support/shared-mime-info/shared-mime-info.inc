SUMMARY = "Shared MIME type database and specification"
HOMEPAGE = "http://freedesktop.org/wiki/Software/shared-mime-info"
SECTION = "base"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libxml2 intltool-native glib-2.0 shared-mime-info-native"

SRC_URI = "http://freedesktop.org/~hadess/shared-mime-info-${PV}.tar.xz"

inherit autotools pkgconfig gettext

EXTRA_OECONF = "--disable-update-mimedb"

FILES_${PN} += "${datadir}/mime"
FILES_${PN}-dev += "${datadir}/pkgconfig/shared-mime-info.pc"

# freedesktop.org.xml is only required when updating the mime database,
# package it separately
PACKAGES =+ "shared-mime-info-data"
FILES_shared-mime-info-data = "${datadir}/mime/packages/freedesktop.org.xml"
RDEPENDS_shared-mime-info-data = "shared-mime-info"

do_install () {
	autotools_do_install

	update-mime-database ${D}${datadir}/mime
}

do_install_class-native () {
	autotools_do_install
}

BBCLASSEXTEND = "native nativesdk"
