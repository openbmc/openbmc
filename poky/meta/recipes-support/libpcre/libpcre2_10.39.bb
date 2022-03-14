DESCRIPTION = "There are two major versions of the PCRE library. The \
newest version is PCRE2, which is a re-working of the original PCRE \
library to provide an entirely new API. The original, very widely \
deployed PCRE library's API and feature are stable, future releases \
 will be for bugfixes only. All new future features will be to PCRE2, \
not the original PCRE 8.x series."
SUMMARY = "Perl Compatible Regular Expressions version 2"
HOMEPAGE = "http://www.pcre.org"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENCE;md5=43cfa999260dd853cd6cb174dc396f3d"

SRC_URI = "https://github.com/PhilipHazel/pcre2/releases/download/pcre2-${PV}/pcre2-${PV}.tar.bz2"

UPSTREAM_CHECK_URI = "https://github.com/PhilipHazel/pcre2/releases"

SRC_URI[sha256sum] = "0f03caf57f81d9ff362ac28cd389c055ec2bf0678d277349a1a4bee00ad6d440"

CVE_PRODUCT = "pcre2"

S = "${WORKDIR}/pcre2-${PV}"

PROVIDES += "pcre2"
DEPENDS += "bzip2 zlib"

BINCONFIG = "${bindir}/pcre2-config"

inherit autotools binconfig-disabled

EXTRA_OECONF = "\
    --enable-newline-is-lf \
    --with-link-size=2 \
    --with-match-limit=10000000 \
    --enable-pcre2-16 \
    --enable-pcre2-32 \
"
CFLAGS += "-D_REENTRANT"
CXXFLAGS:append:powerpc = " -lstdc++"

PACKAGES =+ "libpcre2-16 libpcre2-32 pcre2grep pcre2grep-doc pcre2test pcre2test-doc"

SUMMARY:pcre2grep = "grep utility that uses perl 5 compatible regexes"
SUMMARY:pcre2grep-doc = "grep utility that uses perl 5 compatible regexes - docs"
SUMMARY:pcre2test = "program for testing Perl-comatible regular expressions"
SUMMARY:pcre2test-doc = "program for testing Perl-comatible regular expressions - docs"

FILES:libpcre2-16 = "${libdir}/libpcre2-16.so.*"
FILES:libpcre2-32 = "${libdir}/libpcre2-32.so.*"
FILES:pcre2grep = "${bindir}/pcre2grep"
FILES:pcre2grep-doc = "${mandir}/man1/pcre2grep.1"
FILES:pcre2test = "${bindir}/pcre2test"
FILES:pcre2test-doc = "${mandir}/man1/pcre2test.1"

BBCLASSEXTEND = "native nativesdk"
