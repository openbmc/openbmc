require oprofile.inc

DEPENDS += "virtual/kernel"
DEPENDS_append_powerpc64 = " libpfm4"

SRC_URI[md5sum] = "248c4c069f9476f427fa7195563f9867"
SRC_URI[sha256sum] = "cf759a6de1a6033d5dfc93bda129a9f2e128aecc4238cc657feb0801d1b0366c"

S = "${WORKDIR}/oprofile-${PV}"

