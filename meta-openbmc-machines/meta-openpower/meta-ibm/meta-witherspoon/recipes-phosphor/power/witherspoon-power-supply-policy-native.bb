SUMMARY = "Power supply policy for Witherspoon"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-monitor

SRC_URI += "file://power-supply-policy.yaml"

do_install() {
        install -D ${WORKDIR}/power-supply-policy.yaml ${D}${config_dir}/power-supply-policy.yaml
}
