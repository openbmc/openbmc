BMC_RW_MTD_ibm-ac-server = "bmc"
BMC_RO_MTD_ibm-ac-server = "alt-bmc+bmc"
BMC_KERNEL_MTD_ibm-ac-server = "bmc"

# Enable signature verification
PACKAGECONFIG_append_ibm-ac-server = " verify_signature"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG_append_ibm-ac-server = " sync_bmc_files"
