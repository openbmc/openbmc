SUMMARY = "Perl modules useful for manipulating date and time information"
HOMEPAGE = "https://metacpan.org/release/TimeDate"
DESCRIPTION = "This is the perl5 TimeDate distribution. It requires perl version 5.003 or later."
SECTION = "libs"
# You can redistribute it and/or modify it under the same terms as Perl itself.
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=21;md5=576b7cb41e5e821501a01ed66f0f9d9e"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/G/GB/GBARR/TimeDate-${PV}.tar.gz"

S = "${WORKDIR}/TimeDate-${PV}"

inherit cpan ptest-perl

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "perl-module-carp perl-module-exporter perl-module-strict perl-module-time-local"
RDEPENDS:${PN}-ptest += "perl-module-test-more perl-module-utf8"

SRC_URI[md5sum] = "b1d91153ac971347aee84292ed886c1c"
SRC_URI[sha256sum] = "75bd254871cb5853a6aa0403ac0be270cdd75c9d1b6639f18ecba63c15298e86"
