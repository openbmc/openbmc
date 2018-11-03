SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_yamldir}
        install -d ${DEST}
        install config.yaml ${DEST}/config.yaml
}

