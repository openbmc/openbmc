# UBI-specific configuration for the phosphor-manager-software package

PACKAGECONFIG_append = " ubifs_layout"

RDEPENDS_phosphor-software-manager-updater-ubi += " \
    mtd-utils-ubifs \
"

# Add ubi-fs configs
EXTRA_OEMESON += "-Dactive-bmc-max-allowed=2"
EXTRA_OEMESON += "-Dmedia-dir='/media'"

SYSTEMD_SERVICE_phosphor-software-manager-updater-ubi += " \
    obmc-flash-bmc-ubirw.service \
    obmc-flash-bmc-ubiro@.service \
    obmc-flash-bmc-ubirw-remove.service \
    obmc-flash-bmc-ubiro-remove@.service \
    obmc-flash-bmc-ubiremount.service \
    obmc-flash-bmc-updateubootvars@.service \
    obmc-flash-bmc-cleanup.service \
    obmc-flash-bmc-mirroruboot.service \
"

# Name of the mtd device where the ubi volumes should be created
BMC_RW_MTD ??= "bmc"
BMC_RO_MTD ??= "bmc"
BMC_KERNEL_MTD ??= "bmc"
FLASH_SIZE ?= "32768"
DISTROOVERRIDES .= ":flash-${FLASH_SIZE}"
BMC_RW_SIZE ??= "0x600000"
BMC_RW_SIZE_flash-131072 = "0x2000000"
SYSTEMD_SUBSTITUTIONS += "RW_MTD:${BMC_RW_MTD}:obmc-flash-bmc-ubirw.service"
SYSTEMD_SUBSTITUTIONS += "RO_MTD:${BMC_RO_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "KERNEL_MTD:${BMC_KERNEL_MTD}:obmc-flash-bmc-ubiro@.service"
SYSTEMD_SUBSTITUTIONS += "RW_SIZE:${BMC_RW_SIZE}:obmc-flash-bmc-ubirw.service"

