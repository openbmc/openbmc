SUMMARY = "GNU Project parser generator (yacc replacement)"
DESCRIPTION = "Bison is a general-purpose parser generator that converts an annotated context-free grammar into \
an LALR(1) or GLR parser for that grammar.  Bison is upward compatible with Yacc: all properly-written Yacc \
grammars ought to work with Bison with no change. Anyone familiar with Yacc should be able to use Bison with \
little trouble."
HOMEPAGE = "http://www.gnu.org/software/bison/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"
SECTION = "devel"
DEPENDS = "bison-native flex-native"

PR = "r1"

SRC_URI = "${GNU_MIRROR}/bison/bison-${PV}.tar.gz \
         file://bison-2.3_m4.patch"

SRC_URI[md5sum] = "22327efdd5080e2b1acb6e560a04b43a"
SRC_URI[sha256sum] = "52f78aa4761a74ceb7fdf770f3554dd84308c3b93c4255e3a5c17558ecda293e"

inherit autotools gettext texinfo
acpaths = "-I ${S}/m4"

do_configure_prepend () {
	rm -f ${S}/m4/*gl.m4
	cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/runtime-po/
}
