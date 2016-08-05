SUMMARY = "Phosphor OpenBMC - Debug Tools"
PR = "r1"

inherit packagegroup
inherit obmc-phosphor-license

RDEPENDS_${PN} = " \
    strace \
    "
