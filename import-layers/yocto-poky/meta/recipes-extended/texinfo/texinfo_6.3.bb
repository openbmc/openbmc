SUMMARY = "Documentation system for on-line information and printed output"
DESCRIPTION = "Texinfo is a documentation system that can produce both \
online information and printed output from a single source file. The \
GNU Project uses the Texinfo file format for most of its documentation."
HOMEPAGE = "http://www.gnu.org/software/texinfo/"
SECTION = "console/utils"
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PROVIDES_append_class-native = " texinfo-replacement-native"

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

RDEPENDS_info += "${@compress_pkg(d)}"

DEPENDS = "zlib ncurses texinfo-replacement-native"
DEPENDS_class-native = "zlib-native ncurses-native"

TARGET_PATCH = "file://use_host_makedoc.patch"
TARGET_PATCH_class-native = ""

SRC_URI = "${GNU_MIRROR}/texinfo/${BP}.tar.gz \
           file://texinfo-4.12-zlib.patch \
           file://disable-native-tools.patch \
           file://link-zip.patch \
           file://dont-depend-on-help2man.patch \
           file://0001-Unset-need_charset_alias-when-building-for-musl.patch \
           ${TARGET_PATCH} \
          "

SRC_URI[md5sum] = "9b08daca9bf8eccae9b0f884aba41f9e"
SRC_URI[sha256sum] = "300a6ba4958c2dd4a6d5ce60f0a335daf7e379f5374f276f6ba31a221f02f606"

tex_texinfo = "texmf/tex/texinfo"

inherit gettext autotools

do_configure_prepend () {
	# autotools_do_configure updates po/Makefile.in.in, we also need
	# update po_document.
	cp -f ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/po_document/
}

do_compile_prepend() {
	if [ -d tools ];then
		oe_runmake -C tools/gnulib/lib
	fi
}

do_install_append() {
	mkdir -p ${D}${datadir}/${tex_texinfo}
	install -p -m644 ${S}/doc/texinfo.tex ${S}/doc/txi-??.tex ${D}${datadir}/${tex_texinfo}
	sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/texi2any ${D}${bindir}/pod2texi
}

do_install_append_class-native() {
	install -m 755 info/makedoc ${D}${bindir}
}

PACKAGES += "info info-doc"

FILES_info = "${bindir}/info ${bindir}/infokey ${bindir}/install-info"
FILES_info-doc = "${infodir}/info.info* ${infodir}/dir ${infodir}/info-*.info* \
                  ${mandir}/man1/info.1* ${mandir}/man5/info.5* \
                  ${mandir}/man1/infokey.1* ${mandir}/man1/install-info.1*"

FILES_${PN} = "${bindir}/makeinfo ${bindir}/texi* ${bindir}/pdftexi2dvi ${bindir}/pod2texi ${datadir}/texinfo"
RDEPENDS_${PN} = "perl"
FILES_${PN}-doc = "${infodir}/texinfo* \
                   ${datadir}/${tex_texinfo} \
                   ${mandir}/man1 ${mandir}/man5"

# Lie about providing the Locale::gettext_xs module. It is not actually built,
# but the code will test for it and if not found use Locale::gettext_pp instead.
# However, this causes a file dependency on perl(Locale::gettext_xs) to be
# generated, which must be satisfied.
RPROVIDES_${PN} += "perl(Locale::gettext_xs)"

BBCLASSEXTEND = "native nativesdk"
