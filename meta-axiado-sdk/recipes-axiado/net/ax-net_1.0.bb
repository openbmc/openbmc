# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "Networking packages"
DESCRIPTION = "Networking services"
LICENSE = "CLOSED"

LIC_FILES_CHKSUM ?= "file://${COREBASE}/meta-axiado/COPYING.axiado;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI += "file://config_eip_ns.sh \
            file://eip_fwl.service \
            file://fwl_dmn.py \
            file://stats_1g.sh \
            file://stat_eip.sh \
            file://stat_shim_mac.sh \
            file://stat_phy_vsc.sh \
            file://stat_phy_mvl.sh \
            file://stat_hw_fwl.sh \
            "

inherit obmc-phosphor-systemd
DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"

do_install() {
        install -d ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/config_eip_ns.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/fwl_dmn.py ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stats_1g.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stat_eip.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stat_shim_mac.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stat_phy_vsc.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stat_phy_mvl.sh ${D}/etc/eip/scripts/
        install -m 0777 ${UNPACKDIR}/stat_hw_fwl.sh ${D}/etc/eip/scripts/
}

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

SYSTEMD_SERVICE:${PN} = "eip_fwl.service"

FILES:${PN} += "/etc/eip/scripts/"
FILES:${PN} += "/etc/eip/scripts/config_eip_ns.sh"
FILES:${PN} += "/etc/eip/scripts/fwl_dmn.py"
FILES:${PN} += "/etc/eip/scripts/stats_1g.sh"
FILES:${PN} += "/etc/eip/scripts/stat_eip.sh"
FILES:${PN} += "/etc/eip/scripts/stat_shim_mac.sh"
FILES:${PN} += "/etc/eip/scripts/stat_phy_vsc.sh"
FILES:${PN} += "/etc/eip/scripts/stat_phy_mvl.sh"
FILES:${PN} += "/etc/eip/scripts/stat_hw_fwl.sh"

RDEPENDS_${PN} += "bash"
INSANE_SKIP:${PN} += "usrmerge"
