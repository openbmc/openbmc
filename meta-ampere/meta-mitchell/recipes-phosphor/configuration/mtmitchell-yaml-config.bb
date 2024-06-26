SUMMARY = "YAML configuration for Mt Mitchell"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
inherit allarch

SRC_URI = " \
           file://mtmitchell-ipmi-sensors.yaml \
           file://mtmitchell-ipmi-fru.yaml \
          "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_install() {
    install -m 0644 -D mtmitchell-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
    install -m 0644 -D mtmitchell-ipmi-fru.yaml \
        ${D}${datadir}/${BPN}/ipmi-fru-read.yaml
}

FILES:${PN}-dev = " \
                   ${datadir}/${BPN}/ipmi-sensors.yaml \
                   ${datadir}/${BPN}/ipmi-fru-read.yaml \
                  "

ALLOW_EMPTY:${PN} = "1"
