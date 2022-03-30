SUMMARY = "Traditional Unix macro processor"
HOMEPAGE = "https://www.gnu.org/software/m4/m4.html"
DESCRIPTION = "GNU m4 is an implementation of the traditional Unix macro processor.  It is mostly SVR4 \
compatible although it has some extensions (for example, handling more than 9 positional parameters to macros). \
GNU M4 also has built-in functions for including files, running shell commands, doing arithmetic, etc."

inherit autotools texinfo ptest gettext

SRC_URI = "${GNU_MIRROR}/m4/m4-${PV}.tar.gz \
           file://ac_config_links.patch \
           file://0001-sigsegv-Fix-build-on-ppc-musl.patch \
           "
SRC_URI:append:class-target = " file://run-ptest \
                                file://serial-tests-config.patch \
                              "

SRC_URI[md5sum] = "f4a2b0284d80353b995f8ef2385ed73c"
SRC_URI[sha256sum] = "3be4a26d825ffdfda52a56fc43246456989a3630093cced3fbddf4771ee58a70"

LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464\
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

                ln -s ptest ${D}${libdir}/${BPN}/${BP} 
}


RDEPENDS:${PN}-ptest += "make coreutils diffutils"
RDEPENDS:${PN}-ptest:append:libc-glibc = "\
     locale-base-fr-fr.iso-8859-1 \
"

INSANE_SKIP:${PN}-ptest += "ldflags"
INSANE_SKIP:${PN}-ptest += "rpaths"

