SUMMARY = "Perl module to manipulate and access URI strings"
DESCRIPTION = "This package contains the URI.pm module with friends. \
The module implements the URI class. URI objects can be used to access \
and manipulate the various components that make up these strings."
HOMEPAGE = "https://metacpan.org/dist/URI"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=9944b87af51186f848ae558344aded9f"

SRC_URI = "${CPAN_MIRROR}/authors/id/O/OA/OALDERS/URI-${PV}.tar.gz \
           file://0001-Skip-TODO-test-cases-that-fail.patch \
           "

SRC_URI[sha256sum] = "e7985da359b15efd00917fa720292b711c396f2f9f9a7349e4e7dec74aa79765"

S = "${WORKDIR}/URI-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_install:prepend() {
	# these tests require "-T" (taint) command line option
	rm -rf ${B}/t/cwd.t
	rm -rf ${B}/t/file.t
}

RDEPENDS:${PN} += "\
    perl-module-integer \
    perl-module-mime-base64 \
"

RDEPENDS:${PN}-ptest += " \
    libtest-fatal-perl \
    libtest-needs-perl \
    libtest-warnings-perl \
    perl-module-encode \
    perl-module-encode-encoding \
    perl-module-extutils-makemaker \
    perl-module-extutils-mm-unix \
    perl-module-file-spec-functions \
    perl-module-net-domain \
    perl-module-perlio \
    perl-module-perlio-encoding \
    perl-module-test \
    perl-module-test-more \
    perl-module-utf8 \
"

BBCLASSEXTEND = "native"
