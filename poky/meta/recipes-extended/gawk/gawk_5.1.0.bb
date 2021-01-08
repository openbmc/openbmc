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

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[mpfr] = "--with-mpfr,--without-mpfr, mpfr"

SRC_URI = "${GNU_MIRROR}/gawk/gawk-${PV}.tar.gz \
           file://run-ptest \
           file://0001-Use-cross-AR-during-compile.patch \
"

SRC_URI[md5sum] = "f719bc9966df28e67fc6ebc405e7ea03"
SRC_URI[sha256sum] = "03a0360edcd84bec156fe211bbc4fc8c78790973ce4e8b990a11d778d40b1a26"

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

        sed -i -e "s|GAWKLOCALE|LANG|g" ${D}${PTEST_PATH}/test/Maketests
}

RDEPENDS_${PN}-ptest += "make"

RDEPENDS_${PN}-ptest_append_libc-glibc = "\
     locale-base-en-us.iso-8859-1 \
"

BBCLASSEXTEND = "native nativesdk"
