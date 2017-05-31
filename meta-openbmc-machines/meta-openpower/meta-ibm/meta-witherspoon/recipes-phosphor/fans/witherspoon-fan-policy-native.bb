SUMMARY = "Fan policy for Witherspoon"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-monitor

SRC_URI += "file://fan-policy.yaml"

do_install() {
        install -D ${WORKDIR}/fan-policy.yaml ${D}${config_dir}/fan-policy.yaml
}
