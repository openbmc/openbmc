SUMMARY = "Witherspoon Device Tree Configuration"
DESCRIPTION = "Provides the device tree configuration file for Witherspoon"
PR = "r1"

inherit obmc-phosphor-license
inherit native

SRC_URI += "file://config.yaml"

FILES_${PN} += "${datadir}/devtree"

do_install() {
    install -d ${D}${datadir}/devtree
    install -m 0644 config.yaml ${D}${datadir}/devtree/
}

S = "${WORKDIR}"
