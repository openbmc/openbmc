SUMMARY = "GNU Troff software"
DESCRIPTION = "The groff (GNU troff) software is a typesetting package which reads plain text mixed with \
formatting commands and produces formatted output."
SECTION = "console/utils"
HOMEPAGE = "http://www.gnu.org/software/groff/"
LICENSE = "GPLv2"
PR = "r1"

LIC_FILES_CHKSUM = "file://COPYING;md5=e43fc16fccd8519fba405f0a0ff6e8a3"

SRC_URI = "${GNU_MIRROR}/${BPN}/old/${BP}.tar.gz \
          file://groff-1.18.1.4-remove-mom.patch;striplevel=1 \
          file://man-local.patch \
          file://mdoc-local.patch \
          file://groff-1.18.1.4-fix-bindir.patch \
" 

inherit autotools texinfo

EXTRA_OECONF="--without-x --prefix=${D} --exec-prefix=${D} --bindir=${D}${bindir} --datadir=${D}${datadir} --mandir=${D}${datadir}/man --infodir=${D}${datadir}info --with-appresdir=${D}${datadir}"

SRC_URI[md5sum] = "ceecb81533936d251ed015f40e5f7287"
SRC_URI[sha256sum] = "ff3c7c3b6cae5e8cc5062a144de5eff0022e8e970e1774529cc2d5dde46ce50d"
PARALLEL_MAKE = ""

do_configure (){
    oe_runconf
}

do_install_append() {
	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	for i in afmtodit mmroff; do
		if [ -f ${D}${bindir}/$i ]; then
			sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/$i
		fi
	done

	mkdir -p ${D}${sysconfdir}/groff
	cp -rf ${D}${datadir}/groff/site-tmac/* ${D}${sysconfdir}/groff/
	cp -rf ${D}${datadir}/groff/site-tmac/* ${D}${datadir}/groff/${PV}/tmac/
}

pkg_postinst_${PN}() {
	ln -s tbl $D${bindir}/gtbl
	echo "export GROFF_FONT_PATH=/usr/share/groff/${PV}/font" >> $D${sysconfdir}/profile
	echo "export GROFF_TMAC_PATH=/usr/share/groff/${PV}/tmac" >> $D${sysconfdir}/profile
}

