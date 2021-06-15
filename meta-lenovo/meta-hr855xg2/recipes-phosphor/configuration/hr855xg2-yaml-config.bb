#"Copyright (c) 2019-present Lenovo"

SUMMARY = "YAML configuration for hr855xg2"
PR = "r1"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${LENOVOBASE}/COPYING.BSD;md5=efc72ac5d37ea632ccf0001f56126210"

inherit allarch

SRC_URI_append_hr855xg2 = "file://hr855xg2-ipmi-fru.yaml \
                           file://hr855xg2-ipmi-fru-properties.yaml \
                           file://hr855xg2-ipmi-sensors.yaml \
                           file://hr855xg2-leds.yaml \
                          "

S = "${WORKDIR}"

do_install() {
    install -m 0644 -D hr855xg2-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D hr855xg2-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D hr855xg2-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
    install -m 0644 -D hr855xg2-leds.yaml \
        ${D}${datadir}/${BPN}/led.yaml
}

FILES_${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    ${datadir}/${BPN}/led.yaml \
    "

ALLOW_EMPTY_${PN} = "1"
