SUMMARY = "Package maintenance system from Debian"
LICENSE = "GPLv2.0+"
SECTION = "base"

DEPENDS = "zlib bzip2 perl ncurses"
DEPENDS_class-native = "bzip2-replacement-native zlib-native virtual/update-alternatives-native gettext-native perl-native"
RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} xz run-postinsts perl"
RDEPENDS_${PN}_class-native = "xz-native"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/d/dpkg/"

inherit autotools gettext perlnative pkgconfig systemd

python () {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        pn = d.getVar('PN', True)
        d.setVar('SYSTEMD_SERVICE_%s' % (pn), 'dpkg-configure.service')
}

export PERL = "${bindir}/perl"
PERL_class-native = "${STAGING_BINDIR_NATIVE}/perl-native/perl"

export PERL_LIBDIR = "${libdir}/perl"
PERL_LIBDIR_class-native = "${libdir}/perl-native/perl"

EXTRA_OECONF = "\
		--disable-dselect \
		--enable-start-stop-daemon \
		--with-zlib \
		--with-bz2 \
		--without-liblzma \
		--without-selinux \
		"

EXTRA_OECONF += "TAR=tar"
EXTRA_OECONF_append_class-target = " DEB_HOST_ARCH=${DPKG_ARCH}"

do_configure () {
    echo >> ${S}/m4/compiler.m4
    sed -i -e 's#PERL_LIBDIR=.*$#PERL_LIBDIR="${libdir}/perl"#' ${S}/configure
    autotools_do_configure
}

do_install_append () {
	if [ "${PN}" = "dpkg-native" ]; then
		# update-alternatives doesn't have an offline mode
		rm ${D}${bindir}/update-alternatives
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env nativeperl|' ${D}${bindir}/dpkg-*
	else
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env perl|' ${D}${bindir}/dpkg-*
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','false','true',d)};then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/dpkg-configure.service ${D}${systemd_unitdir}/system/
		sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
			-e 's,@SYSCONFDIR@,${sysconfdir},g' \
			-e 's,@BINDIR@,${bindir},g' \
			-e 's,@SYSTEMD_UNITDIR@,${systemd_unitdir},g' \
			${D}${systemd_unitdir}/system/dpkg-configure.service
	fi
}

PROV = "virtual/update-alternatives"
PROV_class-native = ""

PROVIDES += "${PROV}"

PACKAGES =+ "update-alternatives-dpkg"
FILES_update-alternatives-dpkg = "${bindir}/update-alternatives ${localstatedir}/lib/dpkg/alternatives ${sysconfdir}/alternatives"
RPROVIDES_update-alternatives-dpkg += "update-alternatives"

PACKAGES += "${PN}-perl"
FILES_${PN}-perl = "${libdir}/perl"

# Split out start-stop-daemon to its own package.  Note that it
# is installed in a different directory than the one used for
# the bitbake version.
#
PACKAGES =+ "${PN}-start-stop"
FILES_${PN}-start-stop = "${sbindir}/start-stop-daemon"

RDEPENDS_${PN} += "${PN}-start-stop"


BBCLASSEXTEND = "native"
