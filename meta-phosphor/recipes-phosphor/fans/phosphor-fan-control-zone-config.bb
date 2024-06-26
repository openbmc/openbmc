SUMMARY = "Phosphor fan zone definition default data"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI = "file://zones.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit allarch
inherit phosphor-fan

do_install() {
    install -D zones.yaml ${D}${control_datadir}/zones.yaml
}

FILES:${PN} += "${control_datadir}/zones.yaml"
