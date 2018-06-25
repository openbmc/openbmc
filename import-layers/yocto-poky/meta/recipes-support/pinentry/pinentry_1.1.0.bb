SUMMARY = "Collection of simple PIN or passphrase entry dialogs"
DESCRIPTION = "\
	Pinentry is a collection of simple PIN or passphrase entry dialogs which \
	utilize the Assuan protocol as described by the aegypten project; see \
	http://www.gnupg.org/aegypten/ for details."

HOMEPAGE = "http://www.gnupg.org/related_software/pinentry/index.en.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=cbbd794e2a0a289b9dfcc9f513d1996e"

inherit autotools pkgconfig

DEPENDS = "gettext-native libassuan libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://libassuan_pkgconf.patch \
           file://gpg-error_pkconf.patch \
"

SRC_URI[md5sum] = "3829315cb0a1e9cedc05ffe6def7a2c6"
SRC_URI[sha256sum] = "68076686fa724a290ea49cdf0d1c0c1500907d1b759a3bcbfbec0293e8f56570"

EXTRA_OECONF = "--disable-rpath --disable-dependency-tracking \
                --disable-pinentry-qt5  \
"

PACKAGECONFIG ??= "ncurses libcap"

PACKAGECONFIG[ncurses] = "--enable-ncurses  --with-ncurses-include-dir=${STAGING_INCDIR}, --disable-ncurses, ncurses"
PACKAGECONFIG[libcap] = "--with-libcap, --without-libcap, libcap"
PACKAGECONFIG[qt] = "--enable-pinentry-qt, --disable-pinentry-qt, qt4-x11"
PACKAGECONFIG[gtk2] = "--enable-pinentry-gtk2, --disable-pinentry-gtk2, gtk+ glib-2.0"

#To use libsecret, add meta-gnome
PACKAGECONFIG[secret] = "--enable-libsecret, --disable-libsecret, libsecret"

BBCLASSEXTEND = "native"
