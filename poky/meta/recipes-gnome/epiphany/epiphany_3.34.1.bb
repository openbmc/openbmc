SUMMARY = "WebKit based web browser for GNOME"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/epiphany"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes avahi libnotify gcr \
	   gsettings-desktop-schemas libxml2-native \
	   glib-2.0 glib-2.0-native json-glib libdazzle"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gsettings features_check upstream-version-is-even gettext
REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0002-help-meson.build-disable-the-use-of-yelp.patch \
           "
SRC_URI[archive.md5sum] = "50b50e18d99b19f44cc9cd419c6a4ba8"
SRC_URI[archive.sha256sum] = "a07bc997ad2cbf14d58557bb0b63e92d8c33683680c5e50ea6f9b53b36bec1d9"

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers ${datadir}/metainfo"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme gsettings-desktop-schemas"
