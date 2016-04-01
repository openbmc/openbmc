SUMMARY = "Collection of simple PIN or passphrase entry dialogs"
DESCRIPTION = "\
	Pinentry is a collection of simple PIN or passphrase entry dialogs which \
	utilize the Assuan protocol as described by the aegypten project; see \
	http://www.gnupg.org/aegypten/ for details."

HOMEPAGE = "http://www.gnupg.org/related_software/pinentry/index.en.html"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=cbbd794e2a0a289b9dfcc9f513d1996e"

inherit autotools

DEPENDS = "gettext-native"

SRC_URI = "ftp://ftp.gnupg.org/gcrypt/${BPN}/${BPN}-${PV}.tar.bz2"

SRC_URI[md5sum] = "f51d454f921111b5156a2291cbf70278"
SRC_URI[sha256sum] = "fd8bc1592ceb22bb492b07cb29b1b140bb882c859e6503b974254c0a4b4134d1"

EXTRA_OECONF = "--disable-rpath \
		        --disable-dependency-tracking \
               "

PACKAGECONFIG ??= "ncurses libcap"

PACKAGECONFIG[ncurses] = "--enable-ncurses  --with-ncurses-include-dir=${STAGING_INCDIR}, --disable-ncurses, ncurses"
PACKAGECONFIG[libcap] = "--with-libcap, --without-libcap, libcap"
PACKAGECONFIG[qt4] = "--enable-pinentry-qt4, --disable-pinentry-qt4, qt4-x11"
PACKAGECONFIG[qt4_clipboard] = "--enable-pinentry-qt4-clipboard --enable-pinentry-qt4, --disable-pinentry-qt4-clipboard, qt4-x11"
PACKAGECONFIG[gtk2] = "--enable-pinentry-gtk2, --disable-pinentry-gtk2, gtk+ glib-2.0"

#To use libsecret, add meta-gnome
PACKAGECONFIG[secret] = "--enable-libsecret, --disable-libsecret, libsecret"
