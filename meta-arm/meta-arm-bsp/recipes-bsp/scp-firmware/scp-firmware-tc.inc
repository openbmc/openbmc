# TC specific SCP configuration

FILESEXTRAPATHS:prepend := "${THISDIR}/files/tc:"
SRC_URI:append:tc = " \
    file://0002-tc0-fix-mpmm-config.patch \
    file://0003-tc0-rename-platform-variant-to-platform-feature-set.patch \
    file://0004-tc0-support-platform-feature-set-options-in-firmware.patch \
    "

COMPATIBLE_MACHINE = "(tc?)"

SCP_PLATFORM:tc0 = "tc0"
SCP_PLATFORM:tc1 = "tc1"
FW_TARGETS = "scp"
