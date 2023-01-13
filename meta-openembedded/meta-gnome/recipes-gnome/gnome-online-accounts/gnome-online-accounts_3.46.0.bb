SUMMARY = "GNOME Online Accounts - Single sign-on framework for GNOME"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=34c88b124db5fb2762c1676be7dadd36"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gtk-icon-cache vala features_check

DEPENDS = "gtk+3 gtk+3-native gdk-pixbuf dbus json-glib libxml2 webkitgtk glib-2.0 rest libsecret"

SRC_URI += "file://0001-build-Use-the-appropriate-dependency-object.patch"
SRC_URI[archive.sha256sum] = "5e7859ce4858a6b99d3995ed70527d66e297bb90bbf75ec8780fe9da22c1fcaa"

PACKAGECONFIG_SOUP ?= "soup3"
PACKAGECONFIG ?= "kerberos owncloud lastfm google windows_live ${PACKAGECONFIG_SOUP}"

PACKAGECONFIG[kerberos] = "-Dkerberos=true, -Dkerberos=false,krb5 gcr3"
PACKAGECONFIG[exchange] = "-Dexchange=true, -Dexchange=false"
PACKAGECONFIG[google] = "-Dgoogle=true, -Dgoogle=false"
PACKAGECONFIG[owncloud] = "-Downcloud=true, -Downcloud=false"
PACKAGECONFIG[windows_live] = "-Dwindows_live=true, -Dwindows_live=false"
PACKAGECONFIG[lastfm] = "-Dlastfm=true, -Dlastfm=false"
PACKAGECONFIG[soup2] = ",,libsoup-2.4,,,soup3"
PACKAGECONFIG[soup3] = ",,libsoup-3.0,,,soup2"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/goa-1.0/web-extensions/*.so \
"

# looked into pkg-config file: it is not a bug - they mean it
FILES:${PN}-dev += "${libdir}/goa-1.0/include"
