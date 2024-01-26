
FILESEXTRAPATHS:prepend:aarch64 = "${ARMFILESPATHS}"
SRC_URI:append:aarch64 = " \
    file://0001-Revert-arm64-defconfig-Enable-Tegra-MGBE-driver.patch \
    "
