SUMMARY = "GNOME Online Accounts - Single sign-on framework for GNOME"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=34c88b124db5fb2762c1676be7dadd36"


inherit gnomebase gsettings gobject-introspection gtk-icon-cache vala features_check

DEPENDS = "gdk-pixbuf dbus glib-2.0"

SRC_URI += "file://0001-Replace-filename-with-basename.patch"
SRC_URI[archive.sha256sum] = "418bb9fcffdbd72a98205ad365137617fc1e3551a54de74f6a98d45d266175bf"

PACKAGECONFIG ?= "goabackend kerberos owncloud lastfm google windows_live"

# goabackend requires webkitgtk to be built with gtk+3 and gcr3
PACKAGECONFIG[goabackend] = "-Dgoabackend=true,-Dgoabackend=false,gtk+3 gtk+3-native json-glib libxml2 libsoup rest libsecret webkitgtk3"
PACKAGECONFIG[kerberos] = "-Dkerberos=true, -Dkerberos=false,krb5 gcr3"
PACKAGECONFIG[exchange] = "-Dexchange=true, -Dexchange=false"
PACKAGECONFIG[google] = "-Dgoogle=true, -Dgoogle=false"
PACKAGECONFIG[owncloud] = "-Downcloud=true, -Downcloud=false"
PACKAGECONFIG[windows_live] = "-Dwindows_live=true, -Dwindows_live=false"
PACKAGECONFIG[lastfm] = "-Dlastfm=true, -Dlastfm=false"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/goa-1.0/web-extensions/*.so \
"

# looked into pkg-config file: it is not a bug - they mean it
FILES:${PN}-dev += "${libdir}/goa-1.0/include"
