SUMMARY = "Phosphor zone conditions definition default data"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI = "file://zone_conditions.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan

do_install() {
    install -D zone_conditions.yaml ${D}${control_datadir}/zone_conditions.yaml
}

FILES:${PN} += "${control_datadir}/zone_conditions.yaml"
