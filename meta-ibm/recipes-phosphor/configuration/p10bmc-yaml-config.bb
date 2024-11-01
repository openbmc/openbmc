SUMMARY = "YAML configuration for p10bmc"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://p10bmc-ipmi-fru.yaml \
    file://p10bmc-ipmi-sensors.yaml \
    file://p10bmc-ipmi-inventory-sensors.yaml \
    "

S = "${WORKDIR}"

do_install() {
    install -m 0644 -D p10bmc-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D p10bmc-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
    install -m 0644 -D p10bmc-ipmi-inventory-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
