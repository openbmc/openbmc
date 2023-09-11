
FILESEXTRAPATHS:prepend:aarch64 = "${ARMFILESPATHS}"
SRC_URI:append:aarch64 = " \
    file://0001-Revert-arm64-defconfig-Enable-Tegra-MGBE-driver.patch \
    file://0002-Revert-arm64-defconfig-Add-Nuvoton-NPCM-family-suppo.patch \
    file://0001-gcc-plugins-Reorganize-gimple-includes-for-GCC-13.patch \
    "
