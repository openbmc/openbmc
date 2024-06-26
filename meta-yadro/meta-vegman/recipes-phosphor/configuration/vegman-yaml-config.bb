SUMMARY = "YAML configuration for VEGMAN"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://vegman-ipmi-sensors-static.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -m 0644 -D vegman-ipmi-sensors-static.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors-static.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-sensors-static.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
