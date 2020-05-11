DESCRIPTION = "The PCRE library is a set of functions that implement regular \
expression pattern matching using the same syntax and semantics as Perl 5. PCRE \
has its own native API, as well as a set of wrapper functions that correspond \
to the POSIX regular expression API."
SUMMARY = "Perl Compatible Regular Expressions"
HOMEPAGE = "http://www.pcre.org"
SECTION = "devel"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENCE;md5=3bb381a66a5385b246d4877922e7511e"
SRC_URI = "https://ftp.pcre.org/pub/pcre/pcre-${PV}.tar.bz2 \
           file://fix-pcre-name-collision.patch \
           file://run-ptest \
           file://Makefile \
           "

SRC_URI[md5sum] = "cf7326204cc46c755b5b2608033d9d24"
SRC_URI[sha256sum] = "19108658b23b3ec5058edc9f66ac545ea19f9537234be1ec62b714c84399366d"

CVE_PRODUCT = "pcre"

S = "${WORKDIR}/pcre-${PV}"

PROVIDES += "pcre"
DEPENDS += "bzip2 zlib"

PACKAGECONFIG ??= "pcre8 unicode-properties jit"

PACKAGECONFIG[pcre8] = "--enable-pcre8,--disable-pcre8"
PACKAGECONFIG[pcre16] = "--enable-pcre16,--disable-pcre16"
PACKAGECONFIG[pcre32] = "--enable-pcre32,--disable-pcre32"
PACKAGECONFIG[pcretest-readline] = "--enable-pcretest-libreadline,--disable-pcretest-libreadline,readline,"
PACKAGECONFIG[unicode-properties] = "--enable-unicode-properties,--disable-unicode-properties"
PACKAGECONFIG[jit] = "--enable-jit=auto,--disable-jit"

BINCONFIG = "${bindir}/pcre-config"

inherit autotools binconfig-disabled ptest

EXTRA_OECONF = "--enable-utf"

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
	# Skip the fr_FR locale test. If the locale fr_FR is found, it is tested.
	# If not found, the test is skipped. The test program assumes fr_FR is non-UTF-8
	# locale so the test fails if fr_FR is UTF-8 locale.
	sed -i -e 's:do3=yes:do3=no:g' ${D}${PTEST_PATH}/RunTest 
}

RDEPENDS_${PN}-ptest += "make"
