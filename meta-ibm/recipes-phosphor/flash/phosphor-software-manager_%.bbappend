BMC_RW_MTD:ibm-ac-server = "bmc"
BMC_RW_MTD:p10bmc = "bmc"
BMC_RW_MTD:mihawk = "bmc"
BMC_RO_MTD:ibm-ac-server = "alt-bmc+bmc"
BMC_RO_MTD:p10bmc = "bmc"
BMC_RO_MTD:mihawk = "alt-bmc+bmc"
BMC_KERNEL_MTD:ibm-ac-server = "bmc"
BMC_KERNEL_MTD:p10bmc = "bmc"
BMC_KERNEL_MTD:mihawk = "bmc"

# Enable signature verification
PACKAGECONFIG:append:ibm-ac-server = " verify_signature"
PACKAGECONFIG:append:p10bmc = " verify_signature"
PACKAGECONFIG:append:mihawk = " verify_signature"

# Enable sync of persistent files to the alternate BMC chip
PACKAGECONFIG:append:ibm-ac-server = " sync_bmc_files"
PACKAGECONFIG:append:mihawk = " sync_bmc_files"

# Enable USB code update
PACKAGECONFIG:append:p10bmc = " usb_code_update"

# Enable Side Switch Boot
PACKAGECONFIG:append:p10bmc = " side_switch_on_boot"

