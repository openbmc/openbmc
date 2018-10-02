SUMMARY = "GLib networking extensions"
DESCRIPTION = "glib-networking contains the implementations of certain GLib networking features that cannot be implemented directly in GLib itself because of their dependencies."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/glib-networking/"
BUGTRACKER = "http://bugzilla.gnome.org"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SECTION = "libs"
DEPENDS = "glib-2.0"

SRC_URI[archive.md5sum] = "99867463f182c2767bce0c74bc9cc981"
SRC_URI[archive.sha256sum] = "eaa787b653015a0de31c928e9a17eb57b4ce23c8cf6f277afaec0d685335012f"

PACKAGECONFIG ??= "ca-certificates gnutls"

# No explicit dependency as it works without ca-certificates installed
PACKAGECONFIG[ca-certificates] = "--with-ca-certificates=${sysconfdir}/ssl/certs/ca-certificates.crt,--without-ca-certificates"
PACKAGECONFIG[gnutls] = "--with-gnutls,--without-gnutls,gnutls"
PACKAGECONFIG[libproxy] = "--with-libproxy,--without-libproxy,libproxy"
PACKAGECONFIG[pkcs11] = "--with-pkcs11,--without-pkcs11,p11-kit"

EXTRA_OECONF = "--without-gnome-proxy"

inherit gnomebase gettext upstream-version-is-even gio-module-cache

FILES_${PN} += "${libdir}/gio/modules/libgio*.so ${datadir}/dbus-1/services/"
FILES_${PN}-dev += "${libdir}/gio/modules/libgio*.la"
FILES_${PN}-staticdev += "${libdir}/gio/modules/libgio*.a"
