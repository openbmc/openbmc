SUMMARY = "GNOME Online Accounts - Single sign-on framework for GNOME"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=34c88b124db5fb2762c1676be7dadd36"

GNOMEBASEBUILDCLASS = "autotools"

inherit gnomebase gsettings gobject-introspection gsettings gtk-doc vala gettext upstream-version-is-even features_check

# for webkitgtk
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "glib-2.0"

SRC_URI[archive.sha256sum] = "585c4f979f6f543b77bfdb4fb01eb18ba25c2aec5b7866c676d929616fb2c3fa"

# backend is required for gnome-control-center
PACKAGECONFIG = "backend other"

PACKAGECONFIG[backend] = "--enable-backend,--disable-backend,gtk+3 webkitgtk libsoup-2.4 json-glib libsecret rest libxml2"
PACKAGECONFIG[krb5] = "--enable-kerberos, --disable-kerberos , krb5 gcr"

# no extra dependencies!
PACKAGECONFIG[other] = " \
    --enable-facebook  --enable-foursquare  --enable-exchange  --enable-flickr  --enable-google  --enable-imap-smtp  --enable-owncloud  --enable-windows-live,\
    --disable-facebook --disable-foursquare --disable-exchange --disable-flickr --disable-google --disable-imap-smtp --disable-owncloud --disable-windows-live, \
"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${libdir}/goa-1.0/web-extensions/*.so \
"

# looked into pkg-config file: it is not a bug - they mean it
FILES:${PN}-dev += "${libdir}/goa-1.0/include"
