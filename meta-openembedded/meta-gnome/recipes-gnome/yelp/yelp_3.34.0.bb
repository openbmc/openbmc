SUMMARY = "Help browser for the GNOME desktop"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6e1b9cb787e76d7e6946887a65caa754 \
"

inherit gnomebase itstool autotools-brokensep gsettings gettext gtk-doc features_check mime-xdg

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "776e29bd16424c8712cbf340cfe6429b"
SRC_URI[archive.sha256sum] = "e3d6527c5963d73206891b32f1f23363164be57de248555513bd0be77a7bd045"

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
