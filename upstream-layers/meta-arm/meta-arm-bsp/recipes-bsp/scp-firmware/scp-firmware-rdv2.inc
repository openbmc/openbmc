# RD V2 specific SCP configurations and build instructions

COMPATIBLE_MACHINE:rdv2 = "rdv2"
SCP_PRODUCT_GROUP = "neoverse-rd"
SCP_PLATFORM = "rdn2"

EXTRA_OECMAKE += "-D SCP_PLATFORM_VARIANT=0"

SCP_LOG_LEVEL = "DEBUG"

SRC_URI_SCP_FIRMWARE = "gitsm://gitlab.arm.com/infra-solutions/reference-design/platsw/scp-firmware.git;protocol=https"
SRCBRANCH = "refinfra-bmc"
# Use the RD-INFRA-2024.12.20 tag
SRCREV  = "62bc3f079d1ae5780df1459d4c66de2dbee54359"
