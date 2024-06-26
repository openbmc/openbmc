SUMMARY = "YAML configuration for s6q"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://ipmi-fru.yaml \
    file://ipmi-fru-properties.yaml \
    file://ipmi-sensors.yaml \
    file://ipmi-inventory-sensors.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -m 0644 -D ipmi-fru.yaml ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D ipmi-fru-properties.yaml ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D ipmi-sensors.yaml ${D}${datadir}/${BPN}/ipmi-sensors.yaml
    install -m 0644 -D ipmi-inventory-sensors.yaml ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
