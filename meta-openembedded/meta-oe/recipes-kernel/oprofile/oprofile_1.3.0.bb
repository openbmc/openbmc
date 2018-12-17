require oprofile.inc

DEPENDS += "virtual/kernel"
DEPENDS_append_powerpc64 = " libpfm4"

SRC_URI[md5sum] = "bd998df5521ebedae31e71cd3fb6200b"
SRC_URI[sha256sum] = "95ded8bde1ec39922f0af015981a67aec63e025a501e4dc04cd65d38f73647e6"

S = "${WORKDIR}/oprofile-${PV}"

