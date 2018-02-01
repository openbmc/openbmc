SUMMARY = "WebKit based web browser for GNOME"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes avahi libnotify gcr \
	   gsettings-desktop-schemas gnome-desktop3 libxml2-native \
	   intltool-native glib-2.0 glib-2.0-native"

inherit gnomebase gsettings distro_features_check upstream-version-is-even gettext
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-yelp.m4-drop-the-check-for-itstool.patch"
SRC_URI[archive.md5sum] = "e08762c6bb01c4d291b3d22c7adb1a65"
SRC_URI[archive.sha256sum] = "de7ea87dc450702bde620033f9e2ce807859727d007396d86b09f2b82397fcc2"

EXTRA_OECONF += " --with-distributor-name=${DISTRO}"

do_configure_prepend() {
    sed -i -e s:help::g ${S}/Makefile.am
}

FILES_${PN} += "${datadir}/appdata ${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme"
