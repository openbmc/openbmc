BMC_RW_MTD = "bmc"
BMC_RO_MTD = "alt-bmc+bmc"
BMC_KERNEL_MTD = "bmc"

# Enable signature verification
PACKAGECONFIG_append_witherspoon = " verify_signature"
PACKAGECONFIG_append_swift = " verify_signature"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG_append_witherspoon = " sync_bmc_files"
PACKAGECONFIG_append_swift = " sync_bmc_files"
