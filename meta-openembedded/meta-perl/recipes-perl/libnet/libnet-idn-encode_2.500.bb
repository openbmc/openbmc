SUMMARY = "IDN Perl module"
DESCRIPTION = "This module provides an easy-to-use interface for encoding \
               and decoding Internationalized Domain Names (IDNs)."

SECTION = "libs"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ea5704cf65ca4ec6e9e167fb94f14dd"

SRC_URI = "${CPAN_MIRROR}/authors/id/C/CF/CFAERBER/Net-IDN-Encode-${PV}.tar.gz \
           file://Net-IDN-Encode-2.500-use_uvchr_to_utf8_flags_instead_of_uvuni_to_utf8_flags.patch \
"
SRC_URI[sha256sum] = "55453633e3ff24ce325b34bc2c8157b9859962a31ab5cf28bf7ccc1c9b3a3eaa"

S = "${WORKDIR}/Net-IDN-Encode-${PV}"

inherit cpan ptest-perl

do_configure:prepend() {
    perl -pi -e 's/auto_install_now.*//g' Makefile.PL
}

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "perl-module-unicode-normalize perl-module-encode-encoding"
