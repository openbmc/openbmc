HOMEPAGE = "https://metacpan.org/pod/Conf::Libconfig"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI = "https://cpan.metacpan.org/authors/id/C/CN/CNANGEL/Conf-Libconfig-${PV}.tar.gz"
SRC_URI[sha256sum] = "2f13d926a6e51fd549da9ea4ed01277a99748d75236c2a1b5f26f57a1abebe61"

S = "${WORKDIR}/Conf-Libconfig-${PV}"

DEPENDS += "libconfig"

EXTRA_PERLFLAGS = "-I ${PERLHOSTLIB}"

inherit cpan

BBCLASSEXTEND += "native"
