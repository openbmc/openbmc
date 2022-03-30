SUMMARY = "GNU awk text processing utility"
DESCRIPTION = "The GNU version of awk, a text processing utility. \
Awk interprets a special-purpose programming language to do \
quick and easy text pattern matching and reformatting jobs."
HOMEPAGE = "https://www.gnu.org/software/gawk/"
BUGTRACKER  = "bug-gawk@gnu.org"
SECTION = "console/utils"

# gawk <= 3.1.5: GPL-2.0-only
# gawk >= 3.1.6: GPL-3.0-only
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[mpfr] = "--with-mpfr,--without-mpfr, mpfr"

SRC_URI = "${GNU_MIRROR}/gawk/gawk-${PV}.tar.gz \
           file://remove-sensitive-tests.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "6168d8d1dc8f74bd17d9dc22fa9634c49070f232343b744901da15fb4f06bffd"

inherit autotools gettext texinfo update-alternatives

FILES:${PN} += "${datadir}/awk"
FILES:${PN}-dev += "${libdir}/${BPN}/*.la"

ALTERNATIVE:${PN} = "awk"
ALTERNATIVE_TARGET[awk] = "${bindir}/gawk"
ALTERNATIVE_PRIORITY = "100"

do_install:append() {
	# remove the link since we don't package it
	rm ${D}${bindir}/awk
}

inherit ptest

do_install_ptest() {
	mkdir ${D}${PTEST_PATH}/test
	ln -s ${bindir}/gawk ${D}${PTEST_PATH}/gawk
	# The list of tests is all targets in Maketests, apart from the dummy Gt-dummy
	TESTS=$(awk -F: '$1 == "Gt-dummy" { next } /[[:alnum:]]+:$/ { print $1 }' ${S}/test/Maketests)
	for i in $TESTS Maketests inclib.awk; do
		cp ${S}/test/$i* ${D}${PTEST_PATH}/test
	done
	sed -i -e 's|/usr/local/bin|${bindir}|g' \
	    -e 's|#!${base_bindir}/awk|#!${bindir}/awk|g' ${D}${PTEST_PATH}/test/*.awk

	sed -i -e "s|GAWKLOCALE|LANG|g" ${D}${PTEST_PATH}/test/Maketests

	# These tests require an unloaded host as otherwise timing sensitive tests can fail
	# https://bugzilla.yoctoproject.org/show_bug.cgi?id=14371
	rm -f ${D}${PTEST_PATH}/test/time.*
	rm -f ${D}${PTEST_PATH}/test/timeout.*
}

RDEPENDS:${PN}-ptest += "make"

RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-en-us.iso-8859-1"

BBCLASSEXTEND = "native nativesdk"
