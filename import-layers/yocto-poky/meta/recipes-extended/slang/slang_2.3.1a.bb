SUMMARY = "The shared library for the S-Lang extension language"

DESCRIPTION = "S-Lang is an interpreted language and a programming library.  The \
S-Lang language was designed so that it can be easily embedded into \
a program to provide the program with a powerful extension language. \
The S-Lang library, provided in this package, provides the S-Lang \
extension language.  S-Lang's syntax resembles C, which makes it easy \
to recode S-Lang procedures in C if you need to."

HOMEPAGE = "http://www.jedsoft.org/slang/"
SECTION = "libs"
DEPENDS = "ncurses virtual/libiconv"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a52a18a472d4f7e45479b06563717c02"

SRC_URI = "http://www.jedsoft.org/releases/${BPN}/${BP}.tar.bz2 \
           file://no-x.patch \
           file://dont-link-to-host.patch \
           file://test-add-output-in-the-format-result-testname.patch \
           file://terminfo_fixes.patch \
           file://run-ptest \
          "

SRC_URI[md5sum] = "c5235313042ed0e71ec708f7b85ec241"
SRC_URI[sha256sum] = "54f0c3007fde918039c058965dffdfd6c5aec0bad0f4227192cc486021f08c36"

UPSTREAM_CHECK_URI = "http://www.jedsoft.org/releases/slang/"
PREMIRRORS_append = "\n http://www.jedsoft.org/releases/slang/.* http://www.jedsoft.org/releases/slang/old/ \n"

inherit autotools-brokensep ptest
CLEANBROKEN = "1"

EXTRA_OECONF = "--without-onig"
# There's no way to turn off rpaths and slang will -rpath to the default search
# path. Unset RPATH to stop this.
EXTRA_OEMAKE = "RPATH=''"

PACKAGECONFIG ??= "pcre"
PACKAGECONFIG[pcre] = "--with-pcre=${STAGING_DIR_HOST}${prefix},--without-pcre,pcre"
PACKAGECONFIG[png] = "--with-png=${STAGING_DIR_HOST}${prefix},--without-png,libpng"
PACKAGECONFIG[zlib] = "--with-z=${STAGING_DIR_HOST}${prefix},--without-z,zlib"

do_configure_prepend() {
    cd ${S}/autoconf
    # slang keeps configure.ac and rest of autoconf files in autoconf/ directory
    # we have to go there to be able to run gnu-configize cause it expects configure.{in,ac}
    # to be present. Resulting files land in autoconf/autoconf/ so we need to move them.
    gnu-configize --force && mv autoconf/config.* .
    # For the same reason we also need to run autoconf manually.
    autoconf && mv configure ..
    cd ${B}
}

do_compile_ptest() {
	oe_runmake -C src static
	oe_runmake -C src/test sltest
}

do_install_ptest() {
	mkdir ${D}${PTEST_PATH}/test
	for f in Makefile sltest runtests.sh *.sl *.inc; do
		cp ${S}/src/test/$f ${D}${PTEST_PATH}/test/
	done
	sed -e 's/\ \$(TEST_PGM)\.c\ assoc\.c\ list\.c\ \$(SLANGLIB)\/libslang\.a//' \
	    -e '/\$(CC).*(TEST_PGM)/d' \
	    -i ${D}${PTEST_PATH}/test/Makefile

	cp ${S}/slsh/lib/require.sl ${D}${PTEST_PATH}/test/
	sed -i 's/\.\.\/\.\.\/slsh\/lib\/require\.sl/require\.sl/' ${D}${PTEST_PATH}/test/req.sl

	cp ${S}/doc/text/slangfun.txt ${D}${PTEST_PATH}/test/
	sed -i 's/\.\.\/\.\.\/doc\/text\/slangfun\.txt/slangfun\.txt/' ${D}${PTEST_PATH}/test/docfun.sl
}

FILES_${PN} += "${libdir}/${BPN}/v2/modules/ ${datadir}/slsh/"

PARALLEL_MAKE = ""
PARALLEL_MAKEINST = ""

BBCLASSEXTEND = "native"
