SUMMARY = "GNOME Online Accounts - Single sign-on framework for GNOME"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=34c88b124db5fb2762c1676be7dadd36"

GNOMEBASEBUILDCLASS = "autotools"

inherit gnomebase gsettings gobject-introspection gsettings gtk-doc vala gettext upstream-version-is-even features_check

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "glib-2.0"

SRC_URI[archive.md5sum] = "44a37e1f8d4cac1dca0c41b57b49c2d0"
SRC_URI[archive.sha256sum] = "1c8f62990833ca41188dbb80c5e99d99b57a62608ca675bbcd37bc2244742f2e"

# backend is required for gnome-control-center
PACKAGECONFIG = "backend other"

PACKAGECONFIG[backend] = "--enable-backend,--disable-backend,gtk+3 webkitgtk libsoup-2.4 json-glib libsecret rest libxml2"
PACKAGECONFIG[krb5] = "--enable-kerberos, --disable-kerberos , krb5 gcr"

# no extra dependencies!
PACKAGECONFIG[other] = " \
    --enable-facebook  --enable-foursquare  --enable-exchange  --enable-flickr  --enable-google  --enable-imap-smtp  --enable-owncloud  --enable-windows-live,\
    --disable-facebook --disable-foursquare --disable-exchange --disable-flickr --disable-google --disable-imap-smtp --disable-owncloud --disable-windows-live, \
"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/goa-1.0/web-extensions/*.so \
"

# looked into pkg-config file: it is not a bug - they mean it
FILES_${PN}-dev += "${libdir}/goa-1.0/include"
