SUMMARY = "Help browser for the GNOME desktop"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=6e1b9cb787e76d7e6946887a65caa754 \
"
inherit gnomebase itstool gsettings gettext features_check mime-xdg

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "opengl"
ANY_OF_DISTRO_FEATURES = "wayland x11"

SRC_URI[archive.sha256sum] = "5afc8e77308451ee45ee919efee5a154ca3c153a90e2a1b58cebf7827ab88ad0"

DEPENDS += " \
    libxml2-native \
    glib-2.0-native \
    gtk4 \
    libadwaita \
    libxslt \
    sqlite3 \
    webkitgtk \
    yelp-xsl \
"

PACKAGECONFIG ??= "lzma bzip2"
PACKAGECONFIG[lzma] = "-Dlzma=enabled,-Dlzma=disabled,xz"
PACKAGECONFIG[bzip2] = "-Dbzip2=enabled,-Dbzip2=disabled,bzip2"

FILES:${PN} += " \
    ${libdir}/yelp-1/web-process-extensions \
    ${datadir}/metainfo \
    ${datadir}/yelp-xsl \
"

RDEPENDS:${PN} += "yelp-xsl"

CVE_STATUS[CVE-2008-3533] = "cpe-incorrect: The current version (49.2) is not affected. Fixed in 2.24."
