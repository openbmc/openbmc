SUMMARY = "Collection of simple PIN or passphrase entry dialogs"
DESCRIPTION = "\
	Pinentry is a collection of simple PIN or passphrase entry dialogs which \
	utilize the Assuan protocol as described by the aegypten project; see \
	http://www.gnupg.org/aegypten/ for details."

HOMEPAGE = "http://www.gnupg.org/related_software/pinentry/index.en.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gettext-native libassuan libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://libassuan_pkgconf.patch \
           file://gpg-error_pkconf.patch \
"

SRC_URI[sha256sum] = "9b3cd5226e7597f2fded399a3bc659923351536559e9db0826981bca316494de"

inherit autotools pkgconfig

PACKAGECONFIG ??= "ncurses"

PACKAGECONFIG[ncurses] = "--enable-ncurses  --with-ncurses-include-dir=${STAGING_INCDIR}, --disable-ncurses, ncurses"
PACKAGECONFIG[qt] = "--enable-pinentry-qt, --disable-pinentry-qt, qtbase-native qtbase"
PACKAGECONFIG[gtk2] = "--enable-pinentry-gtk2, --disable-pinentry-gtk2, gtk+ glib-2.0"

PACKAGECONFIG[secret] = "--enable-libsecret, --disable-libsecret, libsecret"

EXTRA_OECONF = " \
    --disable-rpath \
"
EXTRA_OECONF:append:libc-musl = " \
    ac_cv_should_define__xopen_source=yes \
"

BBCLASSEXTEND = "native nativesdk"
