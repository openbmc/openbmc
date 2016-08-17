SUMMARY = "WebKit based web browser for GNOME"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libsoup-2.4 webkitgtk gtk+3 iso-codes ca-certificates avahi libnotify gcr libwnck3 \
	   gsettings-desktop-schemas gnome-desktop3 libxml2-native intltool-native"

inherit gnomebase gsettings distro_features_check upstream-version-is-even
# libwnck3 is x11 only
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += "file://0001-yelp.m4-drop-the-check-for-itstool.patch"
SRC_URI[archive.md5sum] = "172b78256100e8d3c629764abd0e1495"
SRC_URI[archive.sha256sum] = "be699d484371111abae754e669187215df73e21533f461e513b79537d7a1c1c1"

EXTRA_OECONF += " --disable-nss --with-distributor-name=${DISTRO}"

do_configure_prepend() {
    touch ${S}/gnome-doc-utils.make
    sed -i -e s:help::g ${S}/Makefile.am
}

FILES_${PN} += "${datadir}/appdata ${datadir}/dbus-1 ${datadir}/gnome-shell/search-providers"
RDEPENDS_${PN} = "iso-codes adwaita-icon-theme"
RRECOMMENDS_${PN} = "ca-certificates"

