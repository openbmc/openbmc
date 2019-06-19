SUMMARY = "GNU awk text processing utility"
DESCRIPTION = "The GNU version of awk, a text processing utility. \
Awk interprets a special-purpose programming language to do \
quick and easy text pattern matching and reformatting jobs."
HOMEPAGE = "https://www.gnu.org/software/gawk/"
BUGTRACKER  = "bug-gawk@gnu.org"
SECTION = "console/utils"

# gawk <= 3.1.5: GPLv2
# gawk >= 3.1.6: GPLv3
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "readline"

PACKAGECONFIG[mpfr] = "--with-mpfr,--without-mpfr, mpfr"

SRC_URI = "${GNU_MIRROR}/gawk/gawk-${PV}.tar.gz \
           file://run-ptest \
"

SRC_URI[md5sum] = "45366249c13aae2bfb00e8e97c3f39b7"
SRC_URI[sha256sum] = "2d686c9a5b7ba292bc3fa56bc84a1959d721a9d57970a76413156d4b8f1fc67d"

inherit autotools gettext texinfo update-alternatives

FILES_${PN} += "${datadir}/awk"
FILES_${PN}-dev += "${libdir}/${BPN}/*.la"

ALTERNATIVE_${PN} = "awk"
ALTERNATIVE_TARGET[awk] = "${bindir}/gawk"
ALTERNATIVE_PRIORITY = "100"

do_install_append() {
	# remove the link since we don't package it
	rm ${D}${bindir}/awk
}

inherit ptest

do_install_ptest() {
	mkdir ${D}${PTEST_PATH}/test
	ln -s ${bindir}/gawk ${D}${PTEST_PATH}/gawk
	for i in `grep -vE "@|^$|#|Gt-dummy" ${S}/test/Maketests |awk -F: '{print $1}'` Maketests inclib.awk; \
	  do cp ${S}/test/$i* ${D}${PTEST_PATH}/test; \
	done
	sed -i -e 's|/usr/local/bin|${bindir}|g' \
	    -e 's|#!${base_bindir}/awk|#!${bindir}/awk|g' ${D}${PTEST_PATH}/test/*.awk
}

RDEPENDS_${PN}-ptest += "make"

BBCLASSEXTEND = "native nativesdk"
