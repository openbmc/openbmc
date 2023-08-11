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

SRC_URI = " \
           https://alpha.gnu.org/gnu/autoconf/autoconf-2.72c.tar.gz \
           file://program_prefix.patch \
           file://autoreconf-exclude.patch \
           file://remove-usr-local-lib-from-m4.patch \
           file://preferbash.patch \
           file://autotest-automake-result-format.patch \
           file://man-host-perl.patch \
	   ${BACKPORTS} \
"
SRC_URI:append:class-native = " file://no-man.patch"

BACKPORTS = "\
           file://backports/0001-mention-prototypes-more-prominently-in-NEWS.patch \
           file://backports/0002-build-run-make-fetch-which-updated-these.patch \
           file://backports/0003-NEWS-Tighten-up-wording.patch \
           file://backports/0004-Cater-to-programs-misusing-AC_EGREP_HEADER.patch \
           file://backports/0006-Fix-timing-bug-on-high-speed-builds.patch \
           file://backports/0007-Support-underquoted-callers-better.patch \
           file://backports/0008-New-script-for-building-inside-Guix-containers.patch \
           file://backports/0009-AC_XENIX_DIR-Rewrite-using-AC_CANONICAL_HOST.patch \
           file://backports/0010-AC_TYPE_UID_T-Rewrite-using-AC_CHECK_TYPE.patch \
           file://backports/0011-Make-AC_PROG_GCC_TRADITIONAL-a-compatibility-alias-f.patch \
           file://backports/0012-Overhaul-AC_TYPE_GETGROUPS-and-AC_FUNC_GETGROUPS.patch \
           file://backports/0013-Fold-AC_C_STRINGIZE-into-AC_PROG_CC.patch \
           file://backports/0014-Remove-the-last-few-internal-uses-of-AC_EGREP_CPP.patch \
           file://backports/0015-Support-circa-early-2022-Gnulib.patch \
           file://backports/0016-Improve-year2038-largefile-option-processing.patch \
           file://backports/0017-AC_SYS_YEAR2038-Fix-configure-failure-on-32-bit-ming.patch \
           file://backports/0018-Document-limitation-of-BusyBox-tr.patch \
           file://backports/0019-AC_SYS_YEAR2038_REQUIRED-Fix-configure-failure-with-.patch \
           file://backports/0020-Tone-down-year-2038-changes.patch \
           file://backports/0021-Port-AC_FUNC_MMAP-to-more-modern-systems.patch \
           file://backports/0022-Fix-port-of-AC_FUNC_MMAP.patch \
           file://backports/0023-Improve-AC_SYS_YEAR2038_RECOMMENDED-diagnostic.patch \
           file://backports/0024-Improve-AC_FUNC_MMAP-comments.patch \
           file://backports/0025-Fix-AC_SYS_LARGEFILE-on-GNU-Linux-alpha-s390x.patch \
           file://backports/0026-Modernize-INSTALL.patch \
           file://backports/0027-doc-fix-broken-cross-refs.patch \
           file://backports/0028-INSTALL-Clarify-build-host-target-and-the-system-typ.patch \
           file://backports/0029-Shorten-and-improve-INSTALL.patch \
"

SRC_URI[sha256sum] = "21b64169c820c6cdf27fc981ca9c2fb615546e5dead92bccf8d92d0784cdd364"

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
