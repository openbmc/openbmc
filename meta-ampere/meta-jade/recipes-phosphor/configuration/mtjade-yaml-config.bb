SUMMARY = "YAML configuration for Mt.Jade"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
inherit allarch

SRC_URI = " \
    file://mtjade-ipmi-sensors.yaml \
    "

S = "${WORKDIR}"

do_install() {
    install -m 0644 -D mtjade-ipmi-sensors.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors.yaml
}

FILES:${PN}-dev = " \
                   ${datadir}/${BPN}/ipmi-sensors.yaml \
                  "

ALLOW_EMPTY:${PN} = "1"
