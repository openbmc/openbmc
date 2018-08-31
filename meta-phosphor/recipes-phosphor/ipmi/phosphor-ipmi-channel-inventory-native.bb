SUMMARY = "Sample channel configuration for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit phosphor-ipmi-host
inherit obmc-phosphor-license

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
