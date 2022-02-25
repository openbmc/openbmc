SUMMARY = "Documentation system for on-line information and printed output"
DESCRIPTION = "Texinfo is a documentation system that can produce both \
online information and printed output from a single source file. The \
GNU Project uses the Texinfo file format for most of its documentation."
HOMEPAGE = "http://www.gnu.org/software/texinfo/"
SECTION = "console/utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

PROVIDES:append:class-native = " texinfo-replacement-native"

def compress_pkg(d):
    if bb.data.inherits_class('compress_doc', d):
         compress = d.getVar("DOC_COMPRESS")
         if compress == "gz":
             return "gzip"
         elif compress == "bz2":
             return "bzip2"
         elif compress == "xz":
             return "xz"
    return ""

RDEPENDS:info += "${@compress_pkg(d)}"

DEPENDS = "zlib ncurses texinfo-replacement-native"
DEPENDS:class-native = "zlib-native ncurses-native"

TARGET_PATCH = "file://use_host_makedoc.patch"
TARGET_PATCH:class-native = ""

SRC_URI = "${GNU_MIRROR}/texinfo/${BP}.tar.gz \
           file://0001-gnulib-Update.patch \
           file://disable-native-tools.patch \
           file://link-zip.patch \
           file://dont-depend-on-help2man.patch \
           ${TARGET_PATCH} \
           "

SRC_URI[sha256sum] = "8e09cf753ad1833695d2bac0f57dc3bd6bcbbfbf279450e1ba3bc2d7fb297d08"

tex_texinfo = "texmf/tex/texinfo"

inherit gettext autotools multilib_script

MULTILIB_SCRIPTS = "${PN}:${bindir}/texi2any"

EXTRA_AUTORECONF += "-I ${S}/gnulib/m4"

do_configure:prepend () {
	# autotools_do_configure updates po/Makefile.in.in, we also need
	# update po_document.
	cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po_document/
}

do_compile:prepend() {
	if [ -d tools ];then
		oe_runmake -C tools/gnulib/lib
	fi
}

do_install:append() {
	mkdir -p ${D}${datadir}/${tex_texinfo}
	install -p -m644 ${S}/doc/texinfo.tex ${S}/doc/txi-??.tex ${D}${datadir}/${tex_texinfo}
	sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/texi2any ${D}${bindir}/pod2texi
}

do_install:append:class-native() {
	install -m 755 info/makedoc ${D}${bindir}
}

PACKAGES += "info info-doc"

FILES:info = "${bindir}/info ${bindir}/infokey ${bindir}/install-info"
FILES:info-doc = "${infodir}/info.info* ${infodir}/dir ${infodir}/info-*.info* \
                  ${mandir}/man1/info.1* ${mandir}/man5/info.5* \
                  ${mandir}/man1/infokey.1* ${mandir}/man1/install-info.1*"

FILES:${PN} = "${bindir}/makeinfo ${bindir}/texi* ${bindir}/pdftexi2dvi ${bindir}/pod2texi ${datadir}/texinfo"
RDEPENDS:${PN} = "perl"
FILES:${PN}-doc = "${infodir}/texinfo* \
                   ${datadir}/${tex_texinfo} \
                   ${mandir}/man1 ${mandir}/man5"

# Lie about providing the Locale::gettext_xs module. It is not actually built,
# but the code will test for it and if not found use Locale::gettext_pp instead.
# However, this causes a file dependency on perl(Locale::gettext_xs) to be
# generated, which must be satisfied.
RPROVIDES:${PN} += "perl(Locale::gettext_xs)"

BBCLASSEXTEND = "native nativesdk"
