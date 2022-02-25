SUMMARY = "Perl module to manipulate and access URI strings"
DESCRIPTION = "This package contains the URI.pm module with friends. \
The module implements the URI class. URI objects can be used to access \
and manipulate the various components that make up these strings."

HOMEPAGE = "http://search.cpan.org/dist/URI/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c453e94fae672800f83bc1bd7a38b53f"

DEPENDS += "perl"

SRC_URI = "http://www.cpan.org/authors/id/E/ET/ETHER/URI-${PV}.tar.gz"

SRC_URI[md5sum] = "cdbbf8f8ccdec5c162c8505077a35c2c"
SRC_URI[sha256sum] = "7e2c6fe3b1d5947da334fa558a96e748aaa619213b85bcdce5b5347d4d26c46e"

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

RDEPENDS:${PN} += "perl-module-integer perl-module-mime-base64"
RDEPENDS:${PN}-ptest += " \
    libtest-needs-perl \
    perl-module-test-more \
    perl-module-test \
    perl-module-utf8 \
    perl-module-extutils-makemaker \
    perl-module-net-domain \
    perl-module-encode \
    perl-module-extutils-mm-unix \
    perl-module-file-spec-functions \
    perl-module-perlio \
"

BBCLASSEXTEND = "native"
