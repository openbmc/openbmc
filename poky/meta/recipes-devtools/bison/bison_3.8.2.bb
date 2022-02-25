SUMMARY = "GNU Project parser generator (yacc replacement)"
DESCRIPTION = "Bison is a general-purpose parser generator that converts an annotated context-free grammar into \
an LALR(1) or GLR parser for that grammar.  Bison is upward compatible with Yacc: all properly-written Yacc \
grammars ought to work with Bison with no change. Anyone familiar with Yacc should be able to use Bison with \
little trouble."
HOMEPAGE = "http://www.gnu.org/software/bison/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"
SECTION = "devel"
DEPENDS = "bison-native flex-native"

SRC_URI = "${GNU_MIRROR}/bison/bison-${PV}.tar.xz \
           file://add-with-bisonlocaledir.patch \
           "
SRC_URI[sha256sum] = "9bba0214ccf7f1079c5d59210045227bcf619519840ebfa80cd3849cff5a5bf2"

inherit autotools gettext texinfo

# No point in hardcoding path to m4, just use PATH
CACHED_CONFIGUREVARS = "ac_cv_path_M4=m4"

PACKAGECONFIG ??= "readline ${@ 'textstyle' if d.getVar('USE_NLS') == 'yes' else ''}"
PACKAGECONFIG:class-native ??= ""

# Make readline and textstyle optional. There are recipie for these, but leave them
# disabled for the native recipe. This prevents host contamination of the native tool.
PACKAGECONFIG[readline] = "--with-libreadline-prefix,--without-libreadline-prefix,readline"
PACKAGECONFIG[textstyle] = "--with-libtextstyle-prefix,--without-libtextstyle-prefix,gettext"

# Include the cached configure variables, configure is really good at finding
# libreadline, even if we don't want it.
CACHED_CONFIGUREVARS += "${@bb.utils.contains('PACKAGECONFIG', 'readline', '', ' \
                           ac_cv_header_readline_history_h=no \
                           ac_cv_header_readline_readline_h=no \
                           gl_cv_lib_readline=no', d)} \
                         ${@bb.utils.contains('PACKAGECONFIG', 'textstyle', '', ' \
                           ac_cv_libtextstyle=no', d)}"

# The automatic m4 path detection gets confused, so force the right value
acpaths = "-I ./m4"

do_compile:prepend() {
	for i in mfcalc calc++ rpcalc; do mkdir -p ${B}/examples/$i; done
}

do_install:append:class-native() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${STAGING_DATADIR_NATIVE}/bison
}
do_install:append:class-nativesdk() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${datadir}/bison
}
BBCLASSEXTEND = "native nativesdk"
