# N1 SDP specific configuration for optee-os

require optee-os-generic-n1sdp.inc

TS_INSTALL_PREFIX_PATH = "${RECIPE_SYSROOT}/firmware/sp/opteesp"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/n1sdp:"
SRC_URI:append = " \
    file://0001-plat-n1sdp-add-N1SDP-platform-support.patch \
    file://0002-Handle-logging-syscall.patch \
    file://0003-plat-n1sdp-register-DRAM1-to-optee-os.patch \
    file://0004-plat-n1sdp-add-external-device-tree-base-and-size.patch \
    "

EXTRA_OEMAKE += " CFG_TEE_CORE_LOG_LEVEL=4"

EXTRA_OEMAKE += " CFG_TEE_BENCHMARK=n"

EXTRA_OEMAKE += " CFG_CORE_SEL1_SPMC=y CFG_CORE_FFA=y"

EXTRA_OEMAKE += " CFG_WITH_SP=y"

EXTRA_OEMAKE += " CFG_DT=y"

EXTRA_OEMAKE += " CFG_SECURE_PARTITION=y"

EXTRA_OEMAKE += " CFG_MAP_EXT_DT_SECURE=y"

EXTRA_OEMAKE += " CFG_ENABLE_EMBEDDED_TESTS=y"
