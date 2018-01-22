SUMMARY = "Copy Witherspoon inventory cleanup yaml for inventory manager"
PR = "r1"

inherit native
inherit obmc-phosphor-license
inherit phosphor-inventory-manager

SRC_URI += "file://inventory-cleanup.yaml"

S = "${WORKDIR}"

do_install() {
        install -d ${D}${base_datadir}/events.d/
        install ${S}/inventory-cleanup.yaml ${D}${base_datadir}/events.d/inventory-cleanup.yaml
}
