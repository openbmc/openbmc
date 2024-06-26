SUMMARY = "YAML configuration for Palmetto"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit allarch

SRC_URI = " \
    file://palmetto-ipmi-fru.yaml \
    file://palmetto-ipmi-fru-properties.yaml \
    file://palmetto-ipmi-sensors.yaml \
    "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -m 0644 -D palmetto-ipmi-fru-properties.yaml \
        ${D}${datadir}/${BPN}/ipmi-extra-properties.yaml
    install -m 0644 -D palmetto-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
    install -m 0644 -D palmetto-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES:${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-extra-properties.yaml \
    ${datadir}/${BPN}/ipmi-fru-read.yaml \
    ${datadir}/${BPN}/ipmi-sensors.yaml \
    "

ALLOW_EMPTY:${PN} = "1"
