DESCRIPTION = "Loudmouth is a lightweight and easy-to-use C library for programming with the Jabber protocol."
HOMEPAGE = "http://www.loudmouth-project.org/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=c4f38aef94828f6b280e00d1173be689"

DEPENDS = "glib-2.0 libcheck openssl libidn"

inherit gnomebase gtk-doc

PR = "r2"

SRC_URI += "file://04-use-pkg-config-for-gnutls.patch \
            file://glib-2.32.patch"

SRC_URI[archive.md5sum] = "55339ca42494690c3942ee1465a96937"
SRC_URI[archive.sha256sum] = "95a93f5d009b71ea8193d994aa11f311bc330a3efe1b7cd74dc48f11c7f929e3"
GNOME_COMPRESS_TYPE="bz2"

EXTRA_OECONF = "--with-ssl=openssl"

