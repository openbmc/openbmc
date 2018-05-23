# UBI-specific configuration for the phosphor-manager-software package

PACKAGECONFIG_append = " ubifs_layout"

RDEPENDS_phosphor-software-manager-updater-ubi += " \
    bash \
    mtd-utils-ubifs \
"

FILES_${PN}-updater-ubi += " \
    ${sbindir}/obmc-flash-bmc \
    /usr/local \
    "

SYSTEMD_SERVICE_phosphor-software-manager-updater-ubi += " \
    obmc-flash-bmc-ubirw.service \
    obmc-flash-bmc-ubiro@.service \
    obmc-flash-bmc-setenv@.service \
    obmc-flash-bmc-ubirw-remove.service \
    obmc-flash-bmc-ubiro-remove@.service \
    obmc-flash-bmc-ubiremount.service \
    obmc-flash-bmc-updateubootvars@.service \
    obmc-flash-bmc-cleanup.service \
    obmc-flash-bmc-mirroruboot.service \
    reboot-guard-enable.service \
    reboot-guard-disable.service \
    usr-local.mount \
"

# Name of the mtd device where the ubi volumes should be created
BMC_RW_MTD ??= "bmc"
BMC_RO_MTD ??= "bmc"
BMC_KERNEL_MTD ??= "bmc"
BMC_RW_SIZE ??= "0x600000"
SYSTEMD_SUBSTITUTIONS += "RW_MTD:${BMC_RW_MTD}:obmc-flash-bmc-ubirw.service"
SYSTEMD_SUBSTITUTIONS += "RO_MTD:${BMC_RO_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "KERNEL_MTD:${BMC_KERNEL_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "RW_SIZE:${BMC_RW_SIZE}:obmc-flash-bmc-ubirw.service"

SRC_URI += "file://obmc-flash-bmc"
SRC_URI += "file://synclist"
do_install_append() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/obmc-flash-bmc ${D}${sbindir}/obmc-flash-bmc
    install -d ${D}/usr/local

    if [ -f ${WORKDIR}/build/phosphor-sync-software-manager ]; then
        install -d ${D}${sysconfdir}
        install -m 0644 ${WORKDIR}/synclist ${D}${sysconfdir}/synclist
    fi
}
