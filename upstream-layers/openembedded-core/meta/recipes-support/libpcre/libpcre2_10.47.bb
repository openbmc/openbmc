DESCRIPTION = "There are two major versions of the PCRE library. The \
newest version is PCRE2, which is a re-working of the original PCRE \
library to provide an entirely new API. The original, very widely \
deployed PCRE library's API and feature are stable, future releases \
 will be for bugfixes only. All new future features will be to PCRE2, \
not the original PCRE 8.x series."
SUMMARY = "Perl Compatible Regular Expressions version 2"
HOMEPAGE = "http://www.pcre.org"
SECTION = "devel"
LICENSE = "BSD-3-Clause & BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=6720bf3bcff57543b915c2b22e526df0 \
                    file://deps/sljit/LICENSE;md5=97268427d235c41c0be238ce8e5fda17 \
                    "

BASE_VER = "${@d.getVar('PV').split('+')[0]}"
SRC_URI = "gitsm://github.com/PCRE2Project/pcre2;protocol=https;branch=release/pcre2-${BASE_VER} \
           file://run-ptest \
"
SRCREV = "c084f99ae0af664e86819fd1839a13c56dc0918c"
PV .= "+git"

CVE_PRODUCT = "pcre2"

CVE_STATUS_GROUPS += "CVE_STATUS_STABLE_BACKPORTS"
CVE_STATUS_STABLE_BACKPORTS = "CVE-2026-103111 CVE-2026-86145 CVE-2026-89158 CVE-2026-89160 \
    CVE-2026-89157 CVE-2026-89156 CVE-2026-89161 CVE-2026-89162 \
"
CVE_STATUS_STABLE_BACKPORTS[status] = "cpe-stable-backport: fix available in used git hash"

PROVIDES += "pcre2"
DEPENDS += "bzip2 zlib"

BINCONFIG = "${bindir}/pcre2-config"

inherit autotools binconfig-disabled ptest

EXTRA_OECONF = "\
    --enable-newline-is-lf \
    --with-link-size=2 \
    --with-match-limit=10000000 \
    --enable-pcre2-16 \
    --enable-pcre2-32 \
"
CFLAGS += "-D_REENTRANT"
CXXFLAGS:append:powerpc = " -lstdc++"
LDFLAGS:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', '-Wl,--undefined-version', '', d)}"
LDFLAGS:append:class-nativesdk = " ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', '-Wl,--undefined-version', '', d)}"

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

do_install_ptest() {
    t=${D}${PTEST_PATH}
    cp -r ${S}/testdata $t

    for i in pcre2posix_test pcre2grep pcre2test; do
        "${B}/libtool" --mode=install install "${B}/$i" "$t"
    done
    for i in RunTest RunGrepTest test-driver; \
      do cp ${S}/$i $t; \
    done
}
