SUMMARY = "Shared MIME type database and specification"
DESCRIPTION = "The shared-mime-info package contains the core database of common types and the update-mime-database command used to extend it. It requires glib2 to be installed for building the update command. Additionally, it uses intltool for translations, though this is only a dependency for the maintainers."
HOMEPAGE = "http://freedesktop.org/wiki/Software/shared-mime-info"
SECTION = "base"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libxml2 itstool-native glib-2.0 shared-mime-info-native xmlto-native"

SRC_URI = "git://gitlab.freedesktop.org/xdg/shared-mime-info.git;protocol=https"
SRCREV = "18e558fa1c8b90b86757ade09a4ba4d6a6cf8f70"
PV = "2.1"
S = "${WORKDIR}/git"

inherit meson pkgconfig gettext python3native mime

EXTRA_OEMESON = "-Dupdate-mimedb=true"

FILES_${PN} += "${datadir}/mime"
FILES_${PN}-dev += "${datadir}/pkgconfig/shared-mime-info.pc ${datadir}/gettext/its"

# freedesktop.org.xml is only required when updating the mime database,
# package it separately
PACKAGES =+ "shared-mime-info-data"
FILES_shared-mime-info-data = "${datadir}/mime/packages/freedesktop.org.xml"
RDEPENDS_shared-mime-info-data = "shared-mime-info"

BBCLASSEXTEND = "native nativesdk"
