SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=11;endline=15;md5=1810a9b670f4c85932012bcc2275fc9d"

SRC_URI = "https://cpan.metacpan.org/authors/id/T/TO/TODDR/IO-Tty-${PV}.tar.gz"

SRC_URI[sha256sum] = "d597af221628571cbecf35b44520148c44798dfc8a9867774e60453f79d25ff7"

S = "${UNPACKDIR}/IO-Tty-${PV}"

inherit cpan ptest-perl

RDEPENDS:${PN} += "\
    perl-module-carp \
    perl-module-exporter \
    perl-module-io-handle \
    perl-module-posix \
"

RDEPENDS:${PN}-ptest += "\
    perl-module-test-more \
"

FILES:${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/IO/Tty/.debug/"

