BMC_RW_MTD = "bmc"
BMC_RO_MTD = "alt-bmc+bmc"
BMC_KERNEL_MTD = "bmc"
BMC_RW_SIZE = "0x600000"

# Enable signature verification by DISTRO_FEATURE obmc-ubi-fs
PACKAGECONFIG_append_df-obmc-ubi-fs = " verify_signature"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG_append_df-obmc-ubi-fs = " sync_bmc_files"
