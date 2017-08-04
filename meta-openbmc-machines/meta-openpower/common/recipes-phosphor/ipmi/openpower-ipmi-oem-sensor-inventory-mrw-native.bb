SUMMARY = "OEM sensor config for phosphor-host-ipmid"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-ipmi-host

SRC_URI += "file://config-op.yaml"
SRC_URI += "file://hardcoded-config-op.yaml"

S = "${WORKDIR}"

do_install() {
        DEST=${D}${sensor_yamldir}
        install -d ${DEST}
        install config-op.yaml ${DEST}/config-op.yaml
        install hardcoded-config-op.yaml ${DEST}/hardcoded-config-op.yaml 
}
