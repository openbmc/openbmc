# Total Compute (tc) specific configuration for optee-os and optee-os-tadevkit

FILESEXTRAPATHS:prepend := "${THISDIR}/files/optee-os/tc:"
SRC_URI:append:tc = " \
    file://sp_layout.json \
    file://0001-WIP-Enable-managed-exit.patch \
    file://0002-plat-totalcompute-fix-TZDRAM-start-and-size.patch \
    "

COMPATIBLE_MACHINE = "(tc?)"

OPTEEMACHINE:tc0 = "totalcompute-tc0"
OPTEEMACHINE:tc1 = "totalcompute-tc1"

