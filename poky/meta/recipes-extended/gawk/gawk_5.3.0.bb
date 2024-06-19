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
           file://0001-m4-readline-add-missing-includes.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "378f8864ec21cfceaa048f7e1869ac9b4597b449087caf1eb55e440d30273336"

inherit autotools gettext texinfo update-alternatives

FILES:${PN} += "${datadir}/awk"
FILES:${PN}-dev += "${libdir}/${BPN}/*.la"

PACKAGES =+ "${PN}-gawkbug"
FILES:${PN}-gawkbug += "${bindir}/gawkbug"

ALTERNATIVE:${PN} = "awk"
ALTERNATIVE_TARGET[awk] = "${bindir}/gawk"
ALTERNATIVE_PRIORITY = "100"

do_install:append() {
	# remove the link since we don't package it
	rm ${D}${bindir}/awk
	# Strip non-reproducible build flags (containing build paths)
	sed -i -e 's|^CC.*|CC=""|g' -e 's|^CFLAGS.*|CFLAGS=""|g' ${D}${bindir}/gawkbug
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
	sed -i \
	    -e 's|#! /bin/gawk|#! ${bindir}/gawk|g' \
	    -e 's|#! /usr/local/bin/gawk|#! ${bindir}/gawk|g' \
	    -e 's|#!${base_bindir}/awk|#!${bindir}/awk|g' ${D}${PTEST_PATH}/test/*.awk

	sed -i -e "s|GAWKLOCALE|LANG|g" ${D}${PTEST_PATH}/test/Maketests

	# These tests require an unloaded host as otherwise timing sensitive tests can fail
	# https://bugzilla.yoctoproject.org/show_bug.cgi?id=14371
	rm -f ${D}${PTEST_PATH}/test/time.*
	rm -f ${D}${PTEST_PATH}/test/timeout.*
	for t in time timeout; do
		echo $t >> ${D}${PTEST_PATH}/test/skipped.txt
	done
}

do_install_ptest:append:libc-musl() {
	# Reported  https://lists.gnu.org/archive/html/bug-gawk/2021-02/msg00005.html
	rm -f ${D}${PTEST_PATH}/test/clos1way6.*
	# Needs en_US.UTF-8 but then does not work with musl
	rm -f ${D}${PTEST_PATH}/test/backsmalls1.*
	# Needs en_US.UTF-8 but then does not work with musl
	rm -f ${D}${PTEST_PATH}/test/commas.*
	# The below two need LANG=C inside the make rule for musl
	rm -f ${D}${PTEST_PATH}/test/rebt8b1.*
	rm -f ${D}${PTEST_PATH}/test/regx8bit.*
	for t in clos1way6 backsmalls1 commas rebt8b1 regx8bit; do
		echo $t >> ${D}${PTEST_PATH}/test/skipped.txt
	done
}

RDEPENDS:${PN}-ptest += "make locale-base-en-us coreutils"

RDEPENDS:${PN}-ptest:append:libc-glibc = " locale-base-en-us.iso-8859-1"
RDEPENDS:${PN}-ptest:append:libc-musl = " musl-locales"

BBCLASSEXTEND = "native nativesdk"
