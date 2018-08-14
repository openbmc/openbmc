SUMMARY = "WebKit based web browser for GNOME"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes avahi libnotify gcr \
	   gsettings-desktop-schemas gnome-desktop3 libxml2-native \
	   glib-2.0 glib-2.0-native json-glib"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gsettings distro_features_check upstream-version-is-even gettext
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0002-help-meson.build-disable-the-use-of-yelp.patch \
           "
SRC_URI[archive.md5sum] = "8449968366a6f9aaff3ac228ddfc7c66"
SRC_URI[archive.sha256sum] = "01b16aa55d312ae0f17d3136f90d8c68ac748715f119412fb1917023c6f630a8"

EXTRA_OEMESON += " -Ddistributor_name=${DISTRO}"

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme"
