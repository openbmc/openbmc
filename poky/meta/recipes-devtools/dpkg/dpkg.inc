SUMMARY = "Package maintenance system from Debian"
LICENSE = "GPL-2.0-or-later"
HOMEPAGE = "https://salsa.debian.org/dpkg-team/dpkg"
DESCRIPTION = "The primary interface for the dpkg suite is the dselect program. A more low-level and less user-friendly interface is available in the form of the dpkg command."
SECTION = "base"

DEPENDS = "zlib bzip2 perl ncurses libmd"
DEPENDS:class-native = "bzip2-replacement-native zlib-native virtual/update-alternatives-native gettext-native perl-native libmd-native"
RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_update-alternatives} perl"
RDEPENDS:${PN}:class-native = ""

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

inherit autotools gettext perlnative pkgconfig perl-version update-alternatives bash-completion

PERL:class-native = "${STAGING_BINDIR_NATIVE}/perl-native/perl"

export PERL_LIBDIR = "${libdir}/perl5/${@get_perl_version(d)}"
PERL_LIBDIR:class-native = "${libdir}/perl-native/perl/${@get_perl_version(d)}"

EXTRA_OECONF = "\
		--disable-dselect \
		--enable-start-stop-daemon \
		--with-libz \
		--with-libbz2 \
		--without-libselinux \
		TAR=tar \
		"

EXTRA_OECONF:append:class-target = " --disable-update-alternatives DEB_HOST_ARCH=${DPKG_ARCH}"
EXTRA_OECONF:append:class-nativesdk = " --disable-update-alternatives DEB_HOST_ARCH=${DPKG_ARCH}"

PACKAGECONFIG = "liblzma"
PACKAGECONFIG[liblzma] = "--with-liblzma,--without-liblzma, xz"


#autotools.bbclass default AUTOTOOLS_AUXDIR is ${S}, we need to under ${S}/build-aux
AUTOTOOLS_AUXDIR = "${S}/build-aux"

do_configure:prepend () {
        mkdir -p ${AUTOTOOLS_AUXDIR}
        # autotools_do_configure updates po/Makefile.in.in, we also need
        # update dselect/po and scripts/po
        cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/dselect/po/
        cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/scripts/po/
}

do_install:append () {
	if [ "${PN}" = "dpkg-native" ]; then
		# update-alternatives doesn't have an offline mode
		rm ${D}${bindir}/update-alternatives
		sed -i -e 's|^#!.*${STAGING_BINDIR_NATIVE}/perl-native.*/perl|#!/usr/bin/env nativeperl|' ${D}${bindir}/dpkg-* ${D}${sbindir}/dpkg-*
	else
		sed -i -e 's|^#!.*${STAGING_BINDIR_NATIVE}/perl-native.*/perl|#!/usr/bin/env perl|' ${D}${bindir}/dpkg-* ${D}${sbindir}/dpkg-*
	fi
}

PROV = "virtual/update-alternatives"
PROV:class-native = ""
PROV:class-nativesdk = ""

PROVIDES += "${PROV}"

FILES:${PN} += "${datadir}/zsh"

PACKAGES =+ "update-alternatives-dpkg"
FILES:update-alternatives-dpkg = "${bindir}/update-alternatives ${localstatedir}/lib/dpkg/alternatives ${sysconfdir}/alternatives"
RPROVIDES:update-alternatives-dpkg += "update-alternatives"

PACKAGES += "${PN}-perl"
FILES:${PN}-perl = "${libdir}/perl5/${@get_perl_version(d)}"

RDEPENDS:${PN}-perl += "perl-module-carp perl-module-constant \
                        perl-module-cwd perl-module-digest \
                        perl-module-digest-md5 perl-module-errno \
                        perl-module-exporter perl-module-fcntl \
                        perl-module-feature perl-module-file-basename \
                        perl-module-file-compare perl-module-file-copy \
                        perl-module-file-find perl-module-file-path \
                        perl-module-file-spec perl-module-file-temp \
                        perl-module-list-util perl-module-overload \
                        perl-module-parent perl-module-storable \
                        perl-module-filehandle perl-module-io \
                        perl-module-io-handle perl-module-io-seekable \
                        perl-module-posix perl-module-scalar-util \
                        perl-module-selectsaver perl-module-symbol \
                        perl-module-term-ansicolor perl-module-tie-handle \
                        perl-module-tie-hash perl-module-storable \
                        perl-module-time-hires perl-module-time-piece \
                        perl-module-xsloader"

# Split out start-stop-daemon to its own package.  Note that it
# is installed in a different directory than the one used for
# the bitbake version.
#
PACKAGES =+ "${PN}-start-stop"
FILES:${PN}-start-stop = "${sbindir}/start-stop-daemon.${BPN}"
ALTERNATIVE:${PN}-start-stop = "start-stop-daemon"
ALTERNATIVE_LINK_NAME[start-stop-daemon] = "${sbindir}/start-stop-daemon"
ALTERNATIVE_PRIORITY = "100"

EXTRA_RDPENDS = "ldconfig"
EXTRA_RDPENDS:libc-musl = ""
RDEPENDS:${PN} += "${PN}-start-stop ${EXTRA_RDPENDS}"

BBCLASSEXTEND = "native nativesdk"
