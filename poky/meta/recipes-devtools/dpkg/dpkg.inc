SUMMARY = "Package maintenance system from Debian"
LICENSE = "GPLv2.0+"
SECTION = "base"

DEPENDS = "zlib bzip2 perl ncurses"
DEPENDS_class-native = "bzip2-replacement-native zlib-native virtual/update-alternatives-native gettext-native perl-native"
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} perl"
RDEPENDS_${PN}_class-native = ""

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/dpkg/"

inherit autotools gettext perlnative pkgconfig perl-version update-alternatives

PERL_class-native = "${STAGING_BINDIR_NATIVE}/perl-native/perl"

export PERL_LIBDIR = "${libdir}/perl/${@get_perl_version(d)}"
PERL_LIBDIR_class-native = "${libdir}/perl-native/perl/${@get_perl_version(d)}"

EXTRA_OECONF = "\
		--disable-dselect \
		--enable-start-stop-daemon \
		--with-libz \
		--with-libbz2 \
		--without-libselinux \
		TAR=tar \
		"

EXTRA_OECONF_append_class-target = " --disable-update-alternatives DEB_HOST_ARCH=${DPKG_ARCH}"

PACKAGECONFIG = "liblzma"
PACKAGECONFIG[liblzma] = "--with-liblzma,--without-liblzma, xz"

do_install_append () {
	if [ "${PN}" = "dpkg-native" ]; then
		# update-alternatives doesn't have an offline mode
		rm ${D}${bindir}/update-alternatives
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env nativeperl|' ${D}${bindir}/dpkg-*
	else
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env perl|' ${D}${bindir}/dpkg-*
	fi
}

PROV = "virtual/update-alternatives"
PROV_class-native = ""

PROVIDES += "${PROV}"

PACKAGES =+ "update-alternatives-dpkg"
FILES_update-alternatives-dpkg = "${bindir}/update-alternatives ${localstatedir}/lib/dpkg/alternatives ${sysconfdir}/alternatives"
RPROVIDES_update-alternatives-dpkg += "update-alternatives"

PACKAGES += "${PN}-perl"
FILES_${PN}-perl = "${libdir}/perl/${@get_perl_version(d)}"

RDEPENDS_${PN}-perl += "perl-module-carp perl-module-constant \
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
FILES_${PN}-start-stop = "${sbindir}/start-stop-daemon.${BPN}"
ALTERNATIVE_${PN}-start-stop = "start-stop-daemon"
ALTERNATIVE_LINK_NAME[start-stop-daemon] = "${sbindir}/start-stop-daemon"
ALTERNATIVE_PRIORITY = "100"

RDEPENDS_${PN} += "${PN}-start-stop"


BBCLASSEXTEND = "native"
