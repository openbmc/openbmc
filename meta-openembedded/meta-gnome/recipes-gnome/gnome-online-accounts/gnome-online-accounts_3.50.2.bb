SUMMARY = "GNOME Online Accounts - Single sign-on framework for GNOME"
LICENSE = "LGPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=34c88b124db5fb2762c1676be7dadd36"

GTKIC_VERSION = "4"
inherit gnomebase gsettings gobject-introspection gtk-icon-cache mime-xdg vala features_check
REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('PACKAGECONFIG', 'goabackend', 'opengl', '', d)}"

DEPENDS += "gdk-pixbuf dbus glib-2.0 gcr"

SRC_URI[archive.sha256sum] = "df16ad975d139c6bfc4ebb2ec8bb8327297a791ef2bf0b977c78076af5faa98e"

PACKAGECONFIG ?= "goabackend kerberos owncloud google windows_live"

PACKAGECONFIG[goabackend] = "-Dgoabackend=true,-Dgoabackend=false,gtk4 libadwaita json-glib libxml2 libsoup rest libsecret webkitgtk"
PACKAGECONFIG[kerberos] = "-Dkerberos=true, -Dkerberos=false,krb5"
PACKAGECONFIG[exchange] = "-Dexchange=true, -Dexchange=false"
PACKAGECONFIG[google] = "-Dgoogle=true, -Dgoogle=false"
PACKAGECONFIG[owncloud] = "-Downcloud=true, -Downcloud=false"
PACKAGECONFIG[windows_live] = "-Dwindows_live=true, -Dwindows_live=false"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/goa-1.0/web-extensions/*.so \
"

# looked into pkg-config file: it is not a bug - they mean it
FILES:${PN}-dev += "${libdir}/goa-1.0/include"
