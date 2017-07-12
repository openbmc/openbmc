SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"

S = "${WORKDIR}"

do_install() {
        install -d ${sensor_yamldir}
        install config.yaml ${sensor_yamldir}/config.yaml
}
