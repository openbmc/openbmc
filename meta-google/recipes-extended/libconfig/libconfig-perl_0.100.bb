HOMEPAGE = "https://metacpan.org/pod/Conf::Libconfig"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/BSD;md5=3775480a712fc46a69647678acb234cb"

SRC_URI = "https://cpan.metacpan.org/authors/id/C/CN/CNANGEL/Conf-Libconfig-${PV}.tar.gz"
SRC_URI[sha256sum] = "2f13d926a6e51fd549da9ea4ed01277a99748d75236c2a1b5f26f57a1abebe61"

S = "${WORKDIR}/Conf-Libconfig-${PV}"

DEPENDS += "libconfig"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan

BBCLASSEXTEND += "native"
