FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://phosphor-gpio-keys.scc"
SRC_URI += "file://phosphor-gpio-keys.cfg"
SRC_URI += "file://phosphor-vlan.scc"
SRC_URI += "file://phosphor-vlan.cfg"
SRC_URI += "file://phosphor-wdt-on-panic.scc"
SRC_URI += "file://phosphor-wdt-on-panic.cfg"
SRC_URI_append_df-obmc-ubi-fs = " file://0001-ARM-dts-Aspeed-Witherspoon-Update-BMC-partitioning.patch"

KERNEL_FEATURES_append = " phosphor-vlan"
KERNEL_FEATURES_remove_qemuall = " phosphor-vlan"
