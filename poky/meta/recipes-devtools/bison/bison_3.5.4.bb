SUMMARY = "GNU Project parser generator (yacc replacement)"
DESCRIPTION = "Bison is a general-purpose parser generator that converts an annotated context-free grammar into \
an LALR(1) or GLR parser for that grammar.  Bison is upward compatible with Yacc: all properly-written Yacc \
grammars ought to work with Bison with no change. Anyone familiar with Yacc should be able to use Bison with \
little trouble."
HOMEPAGE = "http://www.gnu.org/software/bison/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "devel"
DEPENDS = "bison-native flex-native"

SRC_URI = "${GNU_MIRROR}/bison/bison-${PV}.tar.xz \
           file://dont-depend-on-help2man.patch.patch \
           file://add-with-bisonlocaledir.patch \
           file://0001-bison-fix-the-parallel-build.patch \
"
SRC_URI[sha256sum] = "4c17e99881978fa32c05933c5262457fa5b2b611668454f8dc2a695cd6b3720c"

# No point in hardcoding path to m4, just use PATH
EXTRA_OECONF += "M4=m4"

# Reset any loadavg set via environment, it breaks parallel build
# | ../bison-3.5.2/lib/uniwidth/width.c:21:10: fatal error: uniwidth.h: No such file or directory
# |  #include "uniwidth.h"
# |           ^~~~~~~~~~~~
EXTRA_OEMAKE_append = " -l"

inherit autotools gettext texinfo

# The automatic m4 path detection gets confused, so force the right value
acpaths = "-I ./m4"

do_compile_prepend() {
	for i in mfcalc calc++ rpcalc; do mkdir -p ${B}/examples/$i; done
}

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${STAGING_DATADIR_NATIVE}/bison
}
do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${datadir}/bison
}
BBCLASSEXTEND = "native nativesdk"
