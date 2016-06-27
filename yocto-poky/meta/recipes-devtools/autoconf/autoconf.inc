SUMMARY = "A GNU tool that procude shell scripts to automatically configure software"
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
		  perl-module-carp \
		  perl-module-constant \
		  perl-module-errno \
		  perl-module-exporter \
		  perl-module-file-basename \
		  perl-module-file-compare \
		  perl-module-file-copy \
		  perl-module-file-glob \
		  perl-module-file-path \
		  perl-module-file-stat \
		  perl-module-file-find \
		  perl-module-getopt-long \
		  perl-module-io-file \
		  perl-module-posix \
		  perl-module-data-dumper \
		 "
RDEPENDS_${PN}_class-native = "m4-native gnu-config-native"

SRC_URI = "${GNU_MIRROR}/autoconf/autoconf-${PV}.tar.gz \
	   file://program_prefix.patch"

inherit autotools texinfo

CACHED_CONFIGUREVARS += "ac_cv_path_PERL=${USRBINPATH}/perl"

do_configure() {
	oe_runconf
}

do_install_append() {
    rm -rf ${D}${datadir}/emacs
}
