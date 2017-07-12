SUMMARY = "sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config.yaml"


S = "${WORKDIR}"

do_install() {
        echo ${sensor_tempdir}
        install -d ${sensor_tempdir}
        install config.yaml ${sensor_tempdir}/config.yaml
}
