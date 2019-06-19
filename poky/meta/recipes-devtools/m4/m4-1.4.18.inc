SUMMARY = "Traditional Unix macro processor"
HOMEPAGE = "https://www.gnu.org/software/m4/m4.html"
DESCRIPTION = "GNU m4 is an implementation of the traditional Unix macro processor.  It is mostly SVR4 \
compatible although it has some extensions (for example, handling more than 9 positional parameters to macros). \
GNU M4 also has built-in functions for including files, running shell commands, doing arithmetic, etc."

inherit autotools texinfo ptest

SRC_URI = "${GNU_MIRROR}/m4/m4-${PV}.tar.gz \
           file://ac_config_links.patch \
           file://m4-1.4.18-glibc-change-work-around.patch \
           "
SRC_URI_append_class-target = " file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
                                file://run-ptest \
                                file://serial-tests-config.patch \
                                file://0001-test-getopt-posix-fix.patch \
                              "

SRC_URI[md5sum] = "a077779db287adf4e12a035029002d28"
SRC_URI[sha256sum] = "ab2633921a5cd38e48797bf5521ad259bdc4b979078034a3b790d7fec5493fab"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504\
	            file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede"

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

EXTRA_OECONF += "--without-libsigsegv-prefix"

EXTRA_OEMAKE += "'infodir=${infodir}'"

do_compile_ptest() {
	cd ${B}/tests
	sed -i '/^buildtest-TESTS: /c buildtest-TESTS: $(TESTS) $(check_LIBRARIES) $(check_PROGRAMS)' Makefile
	oe_runmake buildtest-TESTS
}

do_install_ptest() {
                cp -r ${B}/tests                        ${D}${PTEST_PATH}
                cp -r ${S}/tests/*                      ${D}${PTEST_PATH}/tests/
                sed -i '/^Makefile:/c Makefile:'        ${D}${PTEST_PATH}/tests/Makefile
                sed -i -e  "s;LOCALE_FR='fr_FR';LOCALE_FR='fr_FR.iso88591';g" \
                       -e  "s;LOCALE_FR_UTF8='none';LOCALE_FR_UTF8='fr_FR.utf8';g" ${D}${PTEST_PATH}/tests/Makefile
                find ${D}${PTEST_PATH} -type f -name *.[hoc] | xargs -i rm {}
                cp ${S}/build-aux/update-copyright  ${D}${PTEST_PATH}/tests/
                sed -i 's;update-copyright;./update-copyright;g' ${D}${PTEST_PATH}/tests/test-update-copyright.sh
                chmod 0755 ${D}${PTEST_PATH}/tests/test-mbrtowc-w32-1.sh  ${D}${PTEST_PATH}/tests/test-spawn-pipe-main \
                     ${D}${PTEST_PATH}/tests/test-spawn-pipe-child ${D}${PTEST_PATH}/tests/test-version-etc \
                     ${D}${PTEST_PATH}/tests/test-xalloc-die
}


RDEPENDS_${PN}-ptest += "make coreutils diffutils"
RDEPENDS_${PN}-ptest_append_libc-glibc = "\
     locale-base-fr-fr.iso-8859-1 \
"

INSANE_SKIP_${PN}-ptest += "ldflags"
INSANE_SKIP_${PN}-ptest += "rpaths"

