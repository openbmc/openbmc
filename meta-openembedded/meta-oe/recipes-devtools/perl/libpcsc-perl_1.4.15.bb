SUMMARY = "Perl interface to the PC/SC smart card library"
DESCRIPTION = "Perl wrapper to the PC/SC smartcard library (pcsc-lite) \
together with some small examples. \
The provided modules are Chipcard::PCSC and Chipcard::PCSC::Card."
HOMEPAGE = "https://metacpan.org/dist/pcsc-perl"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://cpan.metacpan.org/authors/id/W/WH/WHOM/pcsc-perl-${PV}.tar.bz2"
SRC_URI[sha256sum] = "5cc834438739bd6e4e837fb7e10bd8befb809cd185ae6cf4b33e5fa9161f59fe"

UPSTREAM_CHECK_URI = "https://cpan.metacpan.org/authors/id/W/WH/WHOM/"
UPSTREAM_CHECK_REGEX = "pcsc-perl-(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/pcsc-perl-${PV}"

inherit cpan pkgconfig

DEPENDS += "pcsc-lite"

RDEPENDS:${PN} += "perl-module-carp"

BBCLASSEXTEND="native"
