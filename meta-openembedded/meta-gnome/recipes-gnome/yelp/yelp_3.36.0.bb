SUMMARY = "Help browser for the GNOME desktop"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6e1b9cb787e76d7e6946887a65caa754 \
"

inherit gnomebase itstool autotools-brokensep gsettings gettext gtk-doc features_check mime-xdg

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "32f879293f79b4042edc46aa13d82c71"
SRC_URI[archive.sha256sum] = "fd4b3e23d31ad2bebe42ac8f80242b2d9bef51418bf62b59acdf2440bd94ed24"

DEPENDS += " \
    libxml2-native \
    glib-2.0-native \
    gtk+3 \
    appstream-glib \
    libxslt \
    sqlite3 \
    webkitgtk \
    yelp-xsl \
"

do_configure_prepend() {
    export ITSTOOL=${STAGING_BINDIR_NATIVE}/itstool
}

FILES_${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/yelp-xsl \
"

RDEPENDS_${PN} += "yelp-xsl"
