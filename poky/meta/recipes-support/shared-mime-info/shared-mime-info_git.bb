SUMMARY = "Shared MIME type database and specification"
HOMEPAGE = "http://freedesktop.org/wiki/Software/shared-mime-info"
SECTION = "base"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libxml2 itstool-native glib-2.0 shared-mime-info-native"

SRC_URI = "git://gitlab.freedesktop.org/xdg/shared-mime-info.git;protocol=https"
SRCREV = "829b26d85e7d89a0caee03046c3bce373f04c80a"
PV = "1.15"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "Release-(?P<pver>(\d+(\-\d+)+))"
UPSTREAM_VERSION_UNKNOWN = "1"

inherit autotools pkgconfig gettext python3native mime

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

	${B}/update-mime-database ${D}${datadir}/mime
}

BBCLASSEXTEND = "native nativesdk"
