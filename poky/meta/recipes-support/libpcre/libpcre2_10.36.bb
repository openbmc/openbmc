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
LIC_FILES_CHKSUM = "file://LICENCE;md5=60c08fab1357bfe9084b333bc33362d6"

SRC_URI = "https://ftp.pcre.org/pub/pcre/pcre2-${PV}.tar.bz2"

SRC_URI[sha256sum] = "a9ef39278113542968c7c73a31cfcb81aca1faa64690f400b907e8ab6b4a665c"

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
CXXFLAGS_append_powerpc = " -lstdc++"

PACKAGES =+ "libpcre2-16 libpcre2-32 pcre2grep pcre2grep-doc pcre2test pcre2test-doc"

SUMMARY_pcre2grep = "grep utility that uses perl 5 compatible regexes"
SUMMARY_pcre2grep-doc = "grep utility that uses perl 5 compatible regexes - docs"
SUMMARY_pcre2test = "program for testing Perl-comatible regular expressions"
SUMMARY_pcre2test-doc = "program for testing Perl-comatible regular expressions - docs"

FILES_libpcre2-16 = "${libdir}/libpcre2-16.so.*"
FILES_libpcre2-32 = "${libdir}/libpcre2-32.so.*"
FILES_pcre2grep = "${bindir}/pcre2grep"
FILES_pcre2grep-doc = "${mandir}/man1/pcre2grep.1"
FILES_pcre2test = "${bindir}/pcre2test"
FILES_pcre2test-doc = "${mandir}/man1/pcre2test.1"

BBCLASSEXTEND = "native nativesdk"
