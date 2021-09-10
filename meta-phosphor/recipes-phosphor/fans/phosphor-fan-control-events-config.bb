SUMMARY = "Phosphor zone events definition default data"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch
inherit phosphor-fan

S = "${WORKDIR}"

SRC_URI = "file://events.yaml"

do_install() {
    install -D events.yaml ${D}${control_datadir}/events.yaml
}

FILES:${PN} += "${control_datadir}/events.yaml"
