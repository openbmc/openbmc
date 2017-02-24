DESCRIPTION = "The PCRE library is a set of functions that implement regular \
expression pattern matching using the same syntax and semantics as Perl 5. PCRE \
has its own native API, as well as a set of wrapper functions that correspond \
to the POSIX regular expression API."
SUMMARY = "Perl Compatible Regular Expressions"
HOMEPAGE = "http://www.pcre.org"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b8221cbf43c5587f90ccf228f1185cc2"
SRC_URI = "ftp://ftp.csx.cam.ac.uk/pub/software/programming/pcre/pcre-${PV}.tar.bz2 \
           file://pcre-cross.patch \
           file://fix-pcre-name-collision.patch \
           file://run-ptest \
           file://Makefile \
"

SRC_URI[md5sum] = "e3fca7650a0556a2647821679d81f585"
SRC_URI[sha256sum] = "b858099f82483031ee02092711689e7245586ada49e534a06e678b8ea9549e8b"

S = "${WORKDIR}/pcre-${PV}"

PROVIDES += "pcre"
DEPENDS += "bzip2 zlib"

PACKAGECONFIG ??= "pcre8 unicode-properties"

PACKAGECONFIG[pcre8] = "--enable-pcre8,--disable-pcre8"
PACKAGECONFIG[pcre16] = "--enable-pcre16,--disable-pcre16"
PACKAGECONFIG[pcre32] = "--enable-pcre32,--disable-pcre32"
PACKAGECONFIG[pcretest-readline] = "--enable-pcretest-libreadline,--disable-pcretest-libreadline,readline,"
PACKAGECONFIG[unicode-properties] = "--enable-unicode-properties,--disable-unicode-properties"

BINCONFIG = "${bindir}/pcre-config"

inherit autotools binconfig-disabled ptest

EXTRA_OECONF = "\
    --enable-newline-is-lf \
    --enable-rebuild-chartables \
    --enable-utf \
    --with-link-size=2 \
    --with-match-limit=10000000 \
"

# Set LINK_SIZE in BUILD_CFLAGS given that the autotools bbclass use it to
# set CFLAGS_FOR_BUILD, required for the libpcre build.
BUILD_CFLAGS =+ "-DLINK_SIZE=2 -I${B}"
CFLAGS += "-D_REENTRANT"
CXXFLAGS_append_powerpc = " -lstdc++"

export CCLD_FOR_BUILD ="${BUILD_CCLD}"

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
