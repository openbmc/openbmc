SUMMARY = "Sample channel configuration for phosphor-host-ipmid"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${PHOSPHORBASE}/COPYING.apache-2.0;md5=34400b68072d710fecd0a2940a0d1658"

inherit native
inherit phosphor-ipmi-host

PROVIDES += "virtual/phosphor-ipmi-channel-config"

SRC_URI += "file://channel.yaml"

S = "${WORKDIR}"

do_install() {
        # This recipe is supposed to create an output yaml file with
        # a sample output file.

        DEST=${D}${sensor_datadir}
        install -d ${DEST}
        install channel.yaml ${DEST}/channel.yaml
}
