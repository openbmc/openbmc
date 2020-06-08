SUMMARY = "WebKit based web browser for GNOME"
BUGTRACKER = "https://gitlab.gnome.org/GNOME/epiphany"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes avahi libnotify gcr \
	   gsettings-desktop-schemas libxml2-native \
	   glib-2.0 glib-2.0-native json-glib libdazzle libhandy"

GNOMEBASEBUILDCLASS = "meson"
inherit gnomebase gsettings features_check upstream-version-is-even gettext mime-xdg
REQUIRED_DISTRO_FEATURES = "x11 opengl"

SRC_URI = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive \
           file://0002-help-meson.build-disable-the-use-of-yelp.patch \
           "
SRC_URI[archive.md5sum] = "d66416d0be97ec7789564473337bfe57"
SRC_URI[archive.sha256sum] = "f6f233fb43c6318476d4594707b3fdd5b4127b5e35263e73fbcad8895db8efb6"

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers ${datadir}/metainfo"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme gsettings-desktop-schemas"
