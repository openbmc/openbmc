SUMMARY = "Fan policy for Witherspoon"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-dbus-monitor

SRC_URI += "file://air-cooled.yaml"
SRC_URI += "file://water-cooled.yaml"

do_install() {
        install -D ${WORKDIR}/air-cooled.yaml ${D}${config_dir}/air-cooled.yaml
        install -D ${WORKDIR}/water-cooled.yaml ${D}${config_dir}/water-cooled.yaml
}
