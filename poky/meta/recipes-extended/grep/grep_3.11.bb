SUMMARY = "GNU grep utility"
HOMEPAGE = "http://savannah.gnu.org/projects/grep/"
DESCRIPTION = "Grep searches one or more input files for lines containing a match to a specified pattern. By default, grep prints the matching lines."
BUGTRACKER = "http://savannah.gnu.org/bugs/?group=grep"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

SRC_URI = "${GNU_MIRROR}/grep/grep-${PV}.tar.xz"

SRC_URI[sha256sum] = "1db2aedde89d0dea42b16d9528f894c8d15dae4e190b59aecc78f5a951276eab"

inherit autotools gettext texinfo pkgconfig

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

do_configure:prepend () {
	sed -i -e '1s,#!@SHELL@,#!/bin/sh,' ${S}/src/egrep.sh
	rm -f ${S}/m4/init.m4
}

do_install () {
	autotools_do_install
	if [ "${base_bindir}" != "${bindir}" ]; then
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/grep ${D}${base_bindir}/grep
		mv ${D}${bindir}/egrep ${D}${base_bindir}/egrep
		mv ${D}${bindir}/fgrep ${D}${base_bindir}/fgrep
		rmdir ${D}${bindir}/
	fi
}

inherit update-alternatives

PACKAGECONFIG ??= "pcre"
PACKAGECONFIG[pcre] = "--enable-perl-regexp,--disable-perl-regexp,libpcre2"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN} = "grep egrep fgrep"
ALTERNATIVE_LINK_NAME[grep] = "${base_bindir}/grep"
ALTERNATIVE_LINK_NAME[egrep] = "${base_bindir}/egrep"
ALTERNATIVE_LINK_NAME[fgrep] = "${base_bindir}/fgrep"

BBCLASSEXTEND = "nativesdk"
