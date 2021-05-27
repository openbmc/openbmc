SUMMARY = "GNU Privacy Guard - encryption and signing tools (2.x)"
DESCRIPTION = "A complete and free implementation of the OpenPGP standard \
as defined by RFC4880 (also known as PGP). GnuPG allows you to encrypt \
and sign your data and communications; it features a versatile key \
management system, along with access modules for all kinds of public \
key directories."
HOMEPAGE = "http://www.gnupg.org/"
LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=189af8afca6d6075ba6c9e0aa8077626 \
                    file://COPYING.LGPL3;md5=a2b6bf2cb38ee52619e60f30a1fc7257"

DEPENDS = "npth libassuan libksba zlib bzip2 readline libgcrypt"

inherit autotools gettext texinfo pkgconfig

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://0001-Use-pkg-config-to-find-pth-instead-of-pth-config.patch \
           file://0002-use-pkgconfig-instead-of-npth-config.patch \
           file://0003-dirmngr-uses-libgpg-error.patch \
           file://0004-autogen.sh-fix-find-version-for-beta-checking.patch \
           file://0001-Woverride-init-is-not-needed-with-gcc-9.patch \
           "
SRC_URI_append_class-native = " file://0001-configure.ac-use-a-custom-value-for-the-location-of-.patch \
                                file://relocate.patch"
SRC_URI_append_class-nativesdk = " file://relocate.patch"

SRC_URI[md5sum] = "4ff88920cf52b35db0dedaee87bdbbb1"
SRC_URI[sha256sum] = "04a7c9d48b74c399168ee8270e548588ddbe52218c337703d7f06373d326ca30"

EXTRA_OECONF = "--disable-ldap \
		--disable-ccid-driver \
		--with-zlib=${STAGING_LIBDIR}/.. \
		--with-bzip2=${STAGING_LIBDIR}/.. \
		--with-readline=${STAGING_LIBDIR}/.. \
		--enable-gpg-is-gpg2 \
               "

# A minimal package containing just enough to run gpg+gpgagent (E.g. use gpgme in opkg)
PACKAGES =+ "${PN}-gpg"
FILES_${PN}-gpg = " \
	${bindir}/gpg \
	${bindir}/gpg2 \
	${bindir}/gpg-agent \
"

# Normal package (gnupg) should depend on minimal package (gnupg-gpg)
# to ensure all tools are included. This is done only in non-native
# builds. Native builds don't have sub-packages, so appending RDEPENDS
# in this case breaks recipe parsing.
RDEPENDS_${PN} += "${@ "" if ("native" in d.getVar("PN")) else (d.getVar("PN") + "-gpg")}"

RRECOMMENDS_${PN} = "pinentry"

do_configure_prepend () {
	# Else these could be used in prefernce to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
	rm -f ${S}/m4/ksba.m4
	rm -f ${S}/m4/libgcrypt.m4
}

do_install_append() {
	ln -sf gpg2 ${D}${bindir}/gpg
	ln -sf gpgv2 ${D}${bindir}/gpgv
}

do_install_append_class-native() {
	create_wrappers ${STAGING_BINDIR_NATIVE}
}

do_install_append_class-nativesdk() {
	create_wrappers ${SDKPATHNATIVE}${bindir_nativesdk}
}

create_wrappers() {
	for i in gpg2 gpgconf gpg-agent gpg-connect-agent; do
		create_wrapper ${D}${bindir}/$i GNUPG_BINDIR=$1
	done
}

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[gnutls] = "--enable-gnutls, --disable-gnutls, gnutls"
PACKAGECONFIG[sqlite3] = "--enable-sqlite, --disable-sqlite, sqlite3"

BBCLASSEXTEND = "native nativesdk"
