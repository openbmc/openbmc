SUMMARY = "A GNU tool that produce shell scripts to automatically configure software"
DESCRIPTION = "Autoconf is an extensible package of M4 macros that produce shell scripts to automatically \ 
configure software source code packages. Autoconf creates a configuration script for a package from a template \
file that lists the operating system features that the package can use, in the form of M4 macro calls."
LICENSE = "GPL-3.0-or-later"
HOMEPAGE = "http://www.gnu.org/software/autoconf/"
SECTION = "devel"
DEPENDS = "m4-native autoconf-native automake-native gnu-config-native help2man-native"
DEPENDS:remove:class-native = "autoconf-native automake-native help2man-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=cc3f3a7596cb558bbd9eb7fbaa3ef16c \
		    file://COPYINGv3;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/autoconf/${BP}.tar.gz \
           file://program_prefix.patch \
           file://autoreconf-exclude.patch \
           file://remove-usr-local-lib-from-m4.patch \
           file://preferbash.patch \
           file://autotest-automake-result-format.patch \
           file://man-host-perl.patch \
           file://0001-specify-void-prototype-for-functions-with-no-paramet.patch \
           "
SRC_URI:append:class-native = " file://no-man.patch"

SRC_URI[sha256sum] = "431075ad0bf529ef13cb41e9042c542381103e80015686222b8a9d4abef42a1c"

RDEPENDS:${PN} = "m4 gnu-config \
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
		  perl-module-file-spec \
		  perl-module-file-spec-unix \
		  perl-module-file-stat \
                  perl-module-file-temp \
		  perl-module-getopt-long \
		  perl-module-io-file \
                  perl-module-list-util \
		  perl-module-overloading \
		  perl-module-posix \
                  perl-module-scalar-util \
		  perl-module-symbol \
		  perl-module-thread-queue \
		  perl-module-threads \
		 "
RDEPENDS:${PN}:class-native = "m4-native gnu-config-native hostperl-runtime-native"

inherit autotools texinfo

PERL = "${USRBINPATH}/perl"
PERL:class-native = "/usr/bin/env perl"
PERL:class-nativesdk = "/usr/bin/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

EXTRA_OECONF += "ac_cv_path_M4=m4 ac_cv_prog_TEST_EMACS=no"

# As autoconf installs its own config.* files, ensure that they're always up to date.
update_gnu_config() {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/build-aux
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/build-aux
}
do_configure[prefuncs] += "update_gnu_config"

do_configure:class-native() {
	oe_runconf
}

do_install:append() {
    rm -rf ${D}${datadir}/emacs
}

BBCLASSEXTEND = "native nativesdk"
