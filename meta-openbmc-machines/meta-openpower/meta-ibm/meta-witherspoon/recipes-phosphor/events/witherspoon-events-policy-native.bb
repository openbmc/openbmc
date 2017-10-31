SUMMARY = "Event policy for Witherspoon"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-monitor

SRC_URI += "file://config.yaml"

do_install() {
        install -D ${WORKDIR}/config.yaml ${D}${config_dir}/config.yaml
}

