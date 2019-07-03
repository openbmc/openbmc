SUMMARY = "YAML configuration for FP5280G2"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${INSPURBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit allarch

SRC_URI = " \
    file://fp5280g2-ipmi-fru.yaml \
    file://fp5280g2-ipmi-fru-bmc.yaml \
    file://fp5280g2-ipmi-fru-properties.yaml \
    file://fp5280g2-ipmi-inventory-sensors.yaml \
    file://fp5280g2-ipmi-sensors.yaml \
    "

S = "${WORKDIR}"

do_install() {
    cat fp5280g2-ipmi-fru.yaml fp5280g2-ipmi-fru-bmc.yaml > fru-read.yaml

    install -m 0644 -D fp5280g2-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D fru-read.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D fp5280g2-ipmi-inventory-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
    install -m 0644 -D fp5280g2-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES_${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    "

ALLOW_EMPTY_${PN} = "1"
