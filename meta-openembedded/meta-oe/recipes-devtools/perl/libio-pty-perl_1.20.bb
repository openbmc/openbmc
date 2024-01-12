SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz"

SRC_URI[sha256sum] = "b15309fc85623893289cb9b2b88dfa9ed1e69156b75f29938553a45be6d730af"

S = "${WORKDIR}/IO-Tty-${PV}"

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

