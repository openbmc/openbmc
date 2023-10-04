SUMMARY = "GNU Troff software"
DESCRIPTION = "The groff (GNU troff) software is a typesetting package which reads plain text mixed with \
formatting commands and produces formatted output."
SECTION = "base"
HOMEPAGE = "http://www.gnu.org/software/groff/"
LICENSE = "GPL-3.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/groff/groff-${PV}.tar.gz \
           file://groff-not-search-fonts-on-build-host.patch \
           file://0001-Make-manpages-mulitlib-identical.patch \
           file://0001-build-Fix-Savannah-64681-webpage.ps-deps.patch \
           file://0001-build-meintro_fr.ps-depends-on-tbl.patch \
           "

SRC_URI[sha256sum] = "6b9757f592b7518b4902eb6af7e54570bdccba37a871fddb2d30ae3863511c13"

DEPENDS = "bison-native groff-native"
RDEPENDS:${PN} += "perl sed"

inherit autotools-brokensep texinfo multilib_script pkgconfig

MULTILIB_SCRIPTS = "${PN}:${bindir}/gpinyin ${PN}:${bindir}/grog"

EXTRA_OECONF = "--without-x --with-urw-fonts-dir=/completely/bogus/dir/"
EXTRA_OEMAKE:class-target = "GROFFBIN=groff GROFF_BIN_PATH=${STAGING_BINDIR_NATIVE}"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl' ac_cv_path_BASH_PROG='no' PAGE=A4"

# Delete these generated files since we depend on bison-native
# and regenerate them. Do it deterministically (always).
do_configure:prepend() {
	rm -f ${S}/src/preproc/eqn/eqn.cpp
	rm -f ${S}/src/preproc/eqn/eqn.hpp
}

do_install:append() {
	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	for i in afmtodit mmroff gropdf pdfmom grog; do
		if [ -f ${D}${bindir}/$i ]; then
			sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/$i
		fi
	done
	if [ -e ${D}${libdir}/charset.alias ]; then
		rm -rf ${D}${libdir}/charset.alias
	fi

	# awk is located at /usr/bin/, not /bin/
	SPECIAL_AWK=`find ${D} -name special.awk`
	if [ -f ${SPECIAL_AWK} ]; then
		sed -i -e 's:#!.*awk:#! ${USRBINPATH}/awk:' ${SPECIAL_AWK}
	fi

	# not ship /usr/bin/glilypond and its releated files in embedded target system
	rm -rf ${D}${bindir}/glilypond
	rm -rf ${D}${libdir}/groff/glilypond
	rm -rf ${D}${mandir}/man1/glilypond*

	# not ship /usr/bin/grap2graph and its releated man files
	rm -rf ${D}${bindir}/grap2graph
	rm -rf ${D}${mandir}/man1/grap2graph*

        # strip hosttool path out of generated files
        sed -i -e 's:${HOSTTOOLS_DIR}/::g' ${D}${docdir}/${BP}/examples/hdtbl/*.roff
}

do_install:append:class-native() {
	create_cmdline_wrapper ${D}/${bindir}/groff \
		-F${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/font \
		-M${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/tmac
}

FILES:${PN} += "${libdir}/${BPN}/site-tmac \
                ${libdir}/${BPN}/groffer/"

BBCLASSEXTEND = "native"
