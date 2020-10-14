SUMMARY = "YAML configuration for Mt.Jade"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
inherit allarch

SRC_URI = " \
    file://ipmi-sensors-${MACHINE}.yaml \
    "

S = "${WORKDIR}"

do_install() {
    install -m 0644 -D ipmi-sensors-${MACHINE}.yaml \
        ${D}${datadir}/${BPN}/ipmi-sensors-${MACHINE}.yaml
}

FILES_${PN}-dev = " \
    ${datadir}/${BPN}/ipmi-sensors-${MACHINE}.yaml \
    "

ALLOW_EMPTY_${PN} = "1"
