SUMMARY = "Phosphor fan zone definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://zones.yaml"

do_install() {
    install -D zones.yaml ${D}${control_datadir}/zones.yaml
}

FILES_${PN} += "${control_datadir}/zones.yaml"
