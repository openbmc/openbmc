SUMMARY = "YAML configuration for FP5280G3"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
        file://fp5280g3-ipmi-fru.yaml \
        file://fp5280g3-ipmi-fru-properties.yaml \
        file://fp5280g3-ipmi-inventory-sensors.yaml \
        file://fp5280g3-ipmi-sensors.yaml \
        "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    cat fp5280g3-ipmi-fru.yaml > fru-read.yaml

    install -m 0644 -D fp5280g3-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D fru-read.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D fp5280g3-ipmi-inventory-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
    install -m 0644 -D fp5280g3-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES:${PN}-dev = " \
        ${datadir}/${BPN}/ipmi-extra-properties.yaml \
        ${datadir}/${BPN}/ipmi-fru-read.yaml \
        ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
        ${datadir}/${BPN}/ipmi-sensors.yaml \
        "

ALLOW_EMPTY:${PN} = "1"

