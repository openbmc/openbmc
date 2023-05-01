SUMMARY = "Perl module for pseudo tty IO"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://META.yml;beginline=11;endline=12;md5=b2562f94907eeb42e8ce9d45f628e587"

SRC_URI = "http://www.cpan.org/modules/by-module/IO/IO-Tty-${PV}.tar.gz \
           file://0001-Make-function-checks-more-robust-within-shared-libs.patch \
           "
SRC_URI[sha256sum] = "a5f1a83020bc5b5dd6c1b570f48c7546e0a8f7fac10a068740b03925ad9e14e8"

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

