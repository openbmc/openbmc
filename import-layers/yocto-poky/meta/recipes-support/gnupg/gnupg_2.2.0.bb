SUMMARY = "GNU Privacy Guard - encryption and signing tools (2.x)"
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
          "

SRC_URI[md5sum] = "789f16949fae2d003d387f49e9da4b74"
SRC_URI[sha256sum] = "d4514a0be0f7a1ff263193330019eb4b53c82f0f5e230af3c14df371271a45e6"

EXTRA_OECONF = "--disable-ldap \
		--disable-ccid-driver \
		--with-zlib=${STAGING_LIBDIR}/.. \
		--with-bzip2=${STAGING_LIBDIR}/.. \
		--with-readline=${STAGING_LIBDIR}/.. \
		--enable-gpg-is-gpg2 \
               "
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

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[gnutls] = "--enable-gnutls, --disable-gnutls, gnutls"
PACKAGECONFIG[sqlite3] = "--enable-sqlite, --disable-sqlite, sqlite3"
