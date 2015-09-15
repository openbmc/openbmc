DESCRIPTION = "The PCRE library is a set of functions that implement regular \
expression pattern matching using the same syntax and semantics as Perl 5. PCRE \
has its own native API, as well as a set of wrapper functions that correspond \
to the POSIX regular expression API."
SUMMARY = "Perl Compatible Regular Expressions"
HOMEPAGE = "http://www.pcre.org"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=7e4937814aee14758c1c95b59c80c44d"
SRC_URI = "${SOURCEFORGE_MIRROR}/project/pcre/pcre/${PV}/pcre-${PV}.tar.bz2 \
           file://pcre-cross.patch \
           file://fix-pcre-name-collision.patch \
           file://run-ptest \
           file://Makefile \
"

SRC_URI[md5sum] = "ed91be292cb01d21bc7e526816c26981"
SRC_URI[sha256sum] = "51679ea8006ce31379fb0860e46dd86665d864b5020fc9cd19e71260eef4789d"

S = "${WORKDIR}/pcre-${PV}"

PROVIDES += "pcre"
DEPENDS += "bzip2 zlib"

PACKAGECONFIG[pcretest-readline] = "--enable-pcretest-libreadline,--disable-pcretest-libreadline,readline,"

BINCONFIG = "${bindir}/pcre-config"

inherit autotools binconfig-disabled ptest

EXTRA_OECONF = "\
    --enable-newline-is-lf \
    --enable-rebuild-chartables \
    --enable-utf8 \
    --with-link-size=2 \
    --with-match-limit=10000000 \
"

# Set LINK_SIZE in BUILD_CFLAGS given that the autotools bbclass use it to
# set CFLAGS_FOR_BUILD, required for the libpcre build.
BUILD_CFLAGS =+ "-DLINK_SIZE=2 -I${B}"
CFLAGS += "-D_REENTRANT"
CXXFLAGS_append_powerpc = " -lstdc++"

PACKAGES =+ "libpcrecpp libpcreposix pcregrep pcregrep-doc pcretest pcretest-doc"

SUMMARY_libpcrecpp = "${SUMMARY} - C++ wrapper functions"
SUMMARY_libpcreposix = "${SUMMARY} - C wrapper functions based on the POSIX regex API"
SUMMARY_pcregrep = "grep utility that uses perl 5 compatible regexes"
SUMMARY_pcregrep-doc = "grep utility that uses perl 5 compatible regexes - docs"
SUMMARY_pcretest = "program for testing Perl-comatible regular expressions"
SUMMARY_pcretest-doc = "program for testing Perl-comatible regular expressions - docs"

FILES_libpcrecpp = "${libdir}/libpcrecpp.so.*"
FILES_libpcreposix = "${libdir}/libpcreposix.so.*"
FILES_pcregrep = "${bindir}/pcregrep"
FILES_pcregrep-doc = "${mandir}/man1/pcregrep.1"
FILES_pcretest = "${bindir}/pcretest"
FILES_pcretest-doc = "${mandir}/man1/pcretest.1"

BBCLASSEXTEND = "native nativesdk"

do_install_ptest() {
	t=${D}${PTEST_PATH}
	cp ${WORKDIR}/Makefile $t
	cp -r ${S}/testdata $t
	for i in pcre_stringpiece_unittest pcregrep pcretest; \
	  do cp ${B}/.libs/$i $t; \
	done
	for i in RunTest RunGrepTest test-driver; \
	  do cp ${S}/$i $t; \
	done
}
