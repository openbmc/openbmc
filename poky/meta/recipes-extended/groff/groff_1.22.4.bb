SUMMARY = "GNU Troff software"
DESCRIPTION = "The groff (GNU troff) software is a typesetting package which reads plain text mixed with \
formatting commands and produces formatted output."
SECTION = "base"
HOMEPAGE = "http://www.gnu.org/software/groff/"
LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/groff/groff-${PV}.tar.gz \
	file://0001-replace-perl-w-with-use-warnings.patch \
	file://groff-not-search-fonts-on-build-host.patch \
	file://0001-support-musl.patch \
	file://0001-Include-config.h.patch \
        file://0001-Make-manpages-mulitlib-identical.patch \
"

SRC_URI[md5sum] = "08fb04335e2f5e73f23ea4c3adbf0c5f"
SRC_URI[sha256sum] = "e78e7b4cb7dec310849004fa88847c44701e8d133b5d4c13057d876c1bad0293"

# Remove at the next upgrade
PR = "r1"

DEPENDS = "bison-native"
RDEPENDS_${PN} += "perl sed"

inherit autotools-brokensep texinfo multilib_script pkgconfig

MULTILIB_SCRIPTS = "${PN}:${bindir}/gpinyin ${PN}:${bindir}/groffer ${PN}:${bindir}/grog"

EXTRA_OECONF = "--without-x --without-doc"
PARALLEL_MAKE = ""

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl' ac_cv_path_BASH_PROG='no' PAGE=A4"

# Delete these generated files since we depend on bison-native
# and regenerate them. Do it deterministically (always).
do_configure_prepend() {
	rm -f ${S}/src/preproc/eqn/eqn.cpp
	rm -f ${S}/src/preproc/eqn/eqn.hpp
}

do_install_append() {
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
}

do_install_append_class-native() {
	create_cmdline_wrapper ${D}/${bindir}/groff \
		-F${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/font \
		-M${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/tmac
}

FILES_${PN} += "${libdir}/${BPN}/site-tmac \
                ${libdir}/${BPN}/groffer/"

BBCLASSEXTEND = "native"
