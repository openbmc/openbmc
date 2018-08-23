require ffs.inc

PV = "v0.1.0+git${SRCPV}"

SRCREV = "3ec70fbc458e32eef0d0b1de79688b4dc48cbd57"
SRC_URI += "git://github.com/open-power/ffs.git"

S = "${WORKDIR}/git"
