SUMMARY = "sensor config for phosphor-host-ipmid"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PR = "r1"

SRC_URI += "file://config.yaml"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

inherit phosphor-ipmi-host
inherit native

do_install() {
        DEST=${D}${sensor_yamldir}
        install -d ${DEST}
        install config.yaml ${DEST}/config.yaml
}
