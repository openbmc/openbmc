SUMMARY = "Traditional Unix macro processor"
HOMEPAGE = "https://www.gnu.org/software/m4/m4.html"
DESCRIPTION = "GNU m4 is an implementation of the traditional Unix macro processor.  It is mostly SVR4 \
compatible although it has some extensions (for example, handling more than 9 positional parameters to macros). \
GNU M4 also has built-in functions for including files, running shell commands, doing arithmetic, etc."

inherit autotools texinfo ptest gettext

SRC_URI = "${GNU_MIRROR}/m4/m4-${PV}.tar.gz \
           file://0001-gettext-h-Avoid-gcc-Wformat-security-warnings-with-d.patch \
           "
SRC_URI:append:class-target = " file://run-ptest \
                                file://serial-tests-config.patch \
                              "

SRC_URI[sha256sum] = "6ac4fc31ce440debe63987c2ebbf9d7b6634e67a7c3279257dc7361de8bdb3ef"

LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464\
	            file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede"

EXTRA_OECONF += "--without-libsigsegv-prefix"

EXTRA_OEMAKE += "'infodir=${infodir}'"

do_compile_ptest() {
	cd ${B}/tests
	sed -i '/^buildtest-TESTS: /c buildtest-TESTS: $(TESTS) $(check_LIBRARIES) $(check_PROGRAMS)' Makefile
	oe_runmake CPPFLAGS="-DSRCDIR=\\\"${PTEST_PATH}/tests/\\\"" buildtest-TESTS
}

do_install_ptest() {
                cp -r ${B}/tests                        ${D}${PTEST_PATH}
                cp -r ${S}/tests/*                      ${D}${PTEST_PATH}/tests/
                cp -r ${S}/build-aux                    ${D}${PTEST_PATH}/tests/
                sed -i '/^Makefile:/c Makefile:'        ${D}${PTEST_PATH}/tests/Makefile
                find ${D}${PTEST_PATH} -type f -name *.[hoc] | xargs -i rm {}
                chmod 0755 ${D}${PTEST_PATH}/tests/test-spawn-pipe-main \
                     ${D}${PTEST_PATH}/tests/test-spawn-pipe-child ${D}${PTEST_PATH}/tests/test-version-etc \
                     ${D}${PTEST_PATH}/tests/test-xalloc-die
}

do_install_ptest:append:libc-glibc() {
                sed -i -e  "s;LOCALE_FR='fr_FR';LOCALE_FR='fr_FR.iso88591';g" \
                       -e  "s;LOCALE_FR_UTF8='none';LOCALE_FR_UTF8='fr_FR.utf8';g" ${D}${PTEST_PATH}/tests/Makefile
}

RDEPENDS:${PN}-ptest += "make coreutils diffutils bash locale-base-fr-fr"

RDEPENDS:${PN}-ptest:append:libc-glibc = "\
     locale-base-fr-fr.iso-8859-1 \
     glibc-gconv-iso8859-1 \
"
