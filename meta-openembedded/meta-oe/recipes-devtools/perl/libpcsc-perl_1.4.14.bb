SUMMARY = "Perl interface to the PC/SC smart card library"
DESCRIPTION = "Perl wrapper to the PC/SC smartcard library (pcsc-lite) \
together with some small examples. \
The provided modules are Chipcard::PCSC and Chipcard::PCSC::Card."
HOMEPAGE = "https://metacpan.org/dist/pcsc-perl"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENCE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://cpan.metacpan.org/authors/id/W/WH/WHOM/pcsc-perl-${PV}.tar.bz2"
SRC_URI[md5sum] = "45601505dbb7b27329811ac9bad35fab"
SRC_URI[sha256sum] = "2722b7e5543e4faf3ba1ec6b29a7dfec6d92be1edec09d0a3191992d4d88c69d"

S = "${WORKDIR}/pcsc-perl-${PV}"

inherit cpan pkgconfig

DEPENDS += "pcsc-lite"

RDEPENDS:${PN} += "perl-module-carp"

BBCLASSEXTEND="native"
