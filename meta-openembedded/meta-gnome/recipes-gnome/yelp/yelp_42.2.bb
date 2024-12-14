SUMMARY = "Help browser for the GNOME desktop"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6e1b9cb787e76d7e6946887a65caa754 \
"
GNOMEBASEBUILDCLASS = "autotools"
inherit gnomebase itstool autotools-brokensep gsettings gettext gtk-doc features_check mime-xdg

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.sha256sum] = "a2c5fd0787a9089c722cc66bd0f85cdf7088d870e7b6cc85799f8e5bff9eac4b"

DEPENDS += " \
    libxml2-native \
    glib-2.0-native \
    gtk+3 \
    appstream-glib \
    libxslt \
    sqlite3 \
    libhandy \
    webkitgtk3 \
    yelp-xsl \
"
PACKAGECONFIG_SOUP ?= "soup3"
PACKAGECONFIG ??= "${PACKAGECONFIG_SOUP}"

PACKAGECONFIG[soup2] = "--with-webkit2gtk-4-0,,"
PACKAGECONFIG[soup3] = ",--with-webkit2gtk-4-0,"


do_configure:prepend() {
    export ITSTOOL=${STAGING_BINDIR_NATIVE}/itstool
}

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/yelp-xsl \
"

RDEPENDS:${PN} += "yelp-xsl"

CVE_STATUS[CVE-2008-3533] = "cpe-incorrect: The current version (42.2) is not affected. Fixed in 2.24."
