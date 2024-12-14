SUMMARY = "HTML::Tageset -  data tables useful in parsing HTML"
DESCRIPTION = "This module contains several data tables useful in various \
kinds of HTML parsing operations."
HOMEPAGE = "https://metacpan.org/release/HTML-Tagset"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=62;endline=66;md5=aa91eed6adfe182d2af676954f06a7c9"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PE/PETDANCE/HTML-Tagset-${PV}.tar.gz"
SRC_URI[sha256sum] = "adb17dac9e36cd011f5243881c9739417fd102fce760f8de4e9be4c7131108e2"

S = "${WORKDIR}/HTML-Tagset-${PV}"


inherit cpan ptest-perl


RDEPENDS:${PN} += "perl-module-strict perl-module-vars"

RDEPENDS:${PN}-ptest += "perl-module-test"

do_install:prepend() {
    # requires "-T" (taint) command line option
    rm -rf ${B}/t/pod.t
}

BBCLASSEXTEND = "native"
