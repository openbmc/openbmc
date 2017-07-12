SUMMARY = "OEM sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config-op.yaml"

S = "${WORKDIR}"

do_install() {
        install -d ${sensor_yamldir}
        install config-op.yaml ${sensor_yamldir}/config-op.yaml
}
