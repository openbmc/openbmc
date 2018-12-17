SUMMARY = "A GNU tool that produce shell scripts to automatically configure software"
DESCRIPTION = "Autoconf is an extensible package of M4 macros that produce shell scripts to automatically \ 
configure software source code packages. Autoconf creates a configuration script for a package from a template \
file that lists the operating system features that the package can use, in the form of M4 macro calls."
LICENSE = "GPLv3"
HOMEPAGE = "http://www.gnu.org/software/autoconf/"
SECTION = "devel"
DEPENDS += "m4-native"
DEPENDS_class-native = "m4-native gnu-config-native"
DEPENDS_class-nativesdk = "nativesdk-m4 nativesdk-gnu-config"
RDEPENDS_${PN} = "m4 gnu-config \
		  perl \
		  perl-module-bytes \
		  perl-module-carp \
		  perl-module-constant \
		  perl-module-data-dumper \
		  perl-module-errno \
		  perl-module-exporter \
		  perl-module-file-basename \
		  perl-module-file-compare \
		  perl-module-file-copy \
		  perl-module-file-find \
		  perl-module-file-glob \
		  perl-module-file-path \
		  perl-module-file-stat \
		  perl-module-getopt-long \
		  perl-module-io-file \
		  perl-module-posix \
		  perl-module-thread-queue \
		  perl-module-threads \
		 "
RDEPENDS_${PN}_class-native = "m4-native gnu-config-native"
RDEPENDS_${PN}_class-nativesdk = "\
		  nativesdk-gnu-config \
		  nativesdk-m4 \
		  nativesdk-perl \
		  nativesdk-perl-module-bytes \
		  nativesdk-perl-module-carp \
		  nativesdk-perl-module-constant \
		  nativesdk-perl-module-data-dumper \
		  nativesdk-perl-module-errno \
		  nativesdk-perl-module-exporter \
		  nativesdk-perl-module-file-basename \
		  nativesdk-perl-module-file-compare \
		  nativesdk-perl-module-file-copy \
		  nativesdk-perl-module-file-find \
		  nativesdk-perl-module-file-glob \
		  nativesdk-perl-module-file-path \
		  nativesdk-perl-module-file-stat \
		  nativesdk-perl-module-getopt-long \
		  nativesdk-perl-module-io-file \
		  nativesdk-perl-module-posix \
		  nativesdk-perl-module-thread-queue \
		  nativesdk-perl-module-threads \
                  "

SRC_URI = "${GNU_MIRROR}/autoconf/autoconf-${PV}.tar.gz \
	   file://program_prefix.patch"

inherit autotools texinfo

PERL = "${USRBINPATH}/perl"
PERL_class-native = "/usr/bin/env perl"
PERL_class-nativesdk = "/usr/bin/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

do_configure() {
	# manually install a newer config.guess/.sub
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/build-aux
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/build-aux

	oe_runconf
}

do_install_append() {
    rm -rf ${D}${datadir}/emacs
}
