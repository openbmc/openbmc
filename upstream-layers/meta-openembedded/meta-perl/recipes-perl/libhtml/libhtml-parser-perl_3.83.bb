DESCRIPTION = "This package contains the Parser.pm module with friends."
HOMEPAGE = "https://metacpan.org/release/OALDERS/HTML-Parser-3.83"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=996;endline=1000;md5=cab05784294bb47dc319aeced156e96a"

DEPENDS += "perl"

SRC_URI = "${CPAN_MIRROR}/authors/id/O/OA/OALDERS/HTML-Parser-${PV}.tar.gz"

SRC_URI[sha256sum] = "7278ce9791256132b26a71a5719451844704bb9674b58302c3486df43584f8c0"

S = "${UNPACKDIR}/HTML-Parser-${PV}"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan ptest-perl

do_compile() {
    export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
    cpan_do_compile
}

RDEPENDS:${PN} += "\
    perl-module-exporter \
    perl-module-strict \
    perl-module-vars \
    perl-module-xsloader \
    libhtml-tagset-perl \
"

RDEPENDS:${PN}-ptest += "\
    liburi-perl \
    perl-module-config \
    perl-module-file-spec \
    perl-module-filehandle \
    perl-module-io-file \
    perl-module-selectsaver \
    perl-module-test \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"
