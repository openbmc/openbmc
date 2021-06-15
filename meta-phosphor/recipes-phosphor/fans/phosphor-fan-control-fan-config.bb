SUMMARY = "Phosphor fan definition example data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"
PROVIDES += "virtual/phosphor-fan-control-fan-config"

SRC_URI = "file://fans.yaml"

do_install() {
    install -D fans.yaml ${D}${control_datadir}/fans.yaml
}

FILES_${PN} += "${control_datadir}/fans.yaml"
