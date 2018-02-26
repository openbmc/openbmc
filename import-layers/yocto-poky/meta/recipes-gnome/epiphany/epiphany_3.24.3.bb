SUMMARY = "WebKit based web browser for GNOME"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes avahi libnotify gcr \
	   gsettings-desktop-schemas gnome-desktop3 libxml2-native \
	   glib-2.0 glib-2.0-native json-glib"

inherit gnomebase gsettings distro_features_check upstream-version-is-even gettext
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-yelp.m4-drop-the-check-for-itstool.patch \
            file://0001-bookmarks-Check-for-return-value-of-fread.patch \
           "
SRC_URI[archive.md5sum] = "c0221aec6a08935e6854eaa9de9451ef"
SRC_URI[archive.sha256sum] = "fef51676310d9f37e18c9b2d778254232eb17cccd988c2d1ecf42c7b2963a154"

EXTRA_OECONF += " --with-distributor-name=${DISTRO} --enable-debug=no"

do_configure_prepend() {
    sed -i -e s:help::g ${S}/Makefile.am
}

FILES_${PN} += "${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme"
