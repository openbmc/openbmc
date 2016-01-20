SUMMARY = "OpenBMC IPMI FRU Parser"
DESCRIPTION = "Parse the common/chassis/board/product areas of the FRU data into a VPD dictionary table."
HOMEPAGE = "https://github.com/openbmc/ipmi-fru-parser"
PR = "r1"

inherit obmc-phosphor-license
inherit obmc-phosphor-sdbus-service

SRC_URI += "git://github.com/hramasub/ipmi-fru-parser"

SRCREV = "e097f0cc58c64564ae059f9b02ecb574e4cbb666"

S = "${WORKDIR}/git"

do_install() {
        install -m 0755 -d ${D}${libdir}/ipmi
        install -m 0755 ${S}/*.so ${D}${libdir}/ipmi
}
