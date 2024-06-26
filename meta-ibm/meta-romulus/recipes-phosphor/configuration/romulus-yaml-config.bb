SUMMARY = "YAML configuration for Romulus"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://romulus-ipmi-fru.yaml \
    file://romulus-ipmi-fru-bmc.yaml \
    file://romulus-ipmi-fru-properties.yaml \
    file://romulus-ipmi-inventory-sensors.yaml \
    file://romulus-ipmi-sensors.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    cat romulus-ipmi-fru.yaml romulus-ipmi-fru-bmc.yaml > fru-read.yaml

    install -m 0644 -D romulus-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D fru-read.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D romulus-ipmi-inventory-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-inventory-sensors.yaml
    install -m 0644 -D romulus-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-inventory-sensors.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
