FILESEXTRAPATHS_prepend_mtjade := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://arch \
    file://0002-dt-bindings-vendor-prefixes-Add-Ampere-vendor-prefix.patch \
    file://0003-dt-bindings-i2c-ampere-smpro-Add-binding-for-Ampere-.patch \
    file://0004-hwmon-smpro-Add-Ampere-SMpro-hwmon-driver.patch \
"
