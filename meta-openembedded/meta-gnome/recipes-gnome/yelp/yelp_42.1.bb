SUMMARY = "Help browser for the GNOME desktop"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6e1b9cb787e76d7e6946887a65caa754 \
"

inherit gnomebase itstool autotools-brokensep gsettings gettext gtk-doc features_check mime-xdg

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "25b1146ab8549888a5a8da067f63b470b0f0f800b6ae889cacd114d01d713b41"

DEPENDS += " \
    libxml2-native \
    glib-2.0-native \
    gtk+3 \
    appstream-glib \
    libxslt \
    sqlite3 \
    libhandy \
    webkitgtk \
    yelp-xsl \
"
PACKAGECONFIG ?= ""

# Enable if soup3 is enabled in webkit recipe
PACKAGECONFIG[soup3] = ",--with-webkit2gtk-4-0,"

do_configure:prepend() {
    export ITSTOOL=${STAGING_BINDIR_NATIVE}/itstool
}

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/yelp-xsl \
"

RDEPENDS:${PN} += "yelp-xsl"
