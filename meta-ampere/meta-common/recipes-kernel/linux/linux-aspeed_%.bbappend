FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://arch \
    file://0002-dt-bindings-vendor-prefixes-Add-Ampere-vendor-prefix.patch \
    file://0003-dt-bindings-i2c-ampere-smpro-Add-binding-for-Ampere-.patch \
    file://0004-hwmon-smpro-Add-Ampere-SMpro-hwmon-driver.patch \
    file://0005-bindings-ipmi-Add-binding-for-Aspeed-SSIF-BMC-driver.patch \
    file://0007-drivers-char-ipmi-Add-Aspeed-BMC-SSIF-driver.patch \
    file://0008-i2c-aspeed-Handle-TX_NAK-and-SLAVE_MATCH-together.patch \
    file://0009-i2c-aspeed-workaround-for-slave-s-Unexpected-Ack-on-.patch \
"
