BMC_RW_MTD:ibm-ac-server = "bmc"
BMC_RW_MTD:ibm-enterprise = "bmc"
BMC_RO_MTD:ibm-ac-server = "alt-bmc+bmc"
BMC_RO_MTD:ibm-enterprise = "bmc"
BMC_RW_MTD:sbp1 = "bmc"
BMC_RO_MTD:sbp1 = "alt-bmc"

BMC_KERNEL_MTD:ibm-ac-server = "bmc"
BMC_KERNEL_MTD:ibm-enterprise = "bmc"

# Enable signature verification
PACKAGECONFIG:append:ibm-ac-server = " verify_signature"
PACKAGECONFIG:append:ibm-enterprise = " verify_signature"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG:append:ibm-ac-server = " sync_bmc_files"

# Enable USB code update
PACKAGECONFIG:append:ibm-enterprise = " usb_code_update"

# Enable Side Switch Boot
PACKAGECONFIG:append:ibm-enterprise = " side_switch_on_boot"
PACKAGECONFIG:append:system1 = " side_switch_on_boot"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG:append:sbp1 = " sync_bmc_files static-dual-image"

# Set BMC Minimum Ship Level version format
EXTRA_OEMESON:append:ibm-enterprise = " -Dregex-bmc-msl='([a-z]+[0-9]{2})+([0-9]+).([0-9]+).([0-9]+)'"
