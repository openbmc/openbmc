SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_yamldir}
        install -d ${DEST}
        install config.yaml ${DEST}/config.yaml
}

