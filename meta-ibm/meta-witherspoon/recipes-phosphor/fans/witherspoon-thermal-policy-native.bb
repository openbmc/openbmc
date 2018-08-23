SUMMARY = "Thermal policy for Witherspoon"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-monitor

SRC_URI += "file://thermal-policy.yaml"

do_install() {
        install -D ${WORKDIR}/thermal-policy.yaml ${D}${config_dir}/thermal-policy.yaml
}
