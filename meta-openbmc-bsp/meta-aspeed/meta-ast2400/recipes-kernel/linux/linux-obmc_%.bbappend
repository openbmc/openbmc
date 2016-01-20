FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += "file://defconfig file://hwmon.cfg file://spinor-dts.patch"
SRC_URI += "file://0001-mtd-spi-nor-Add-SPI-memory-controllers-for-ASPEED-AS.patch"
SRC_URI += "file://0001-rtc-aspeed-month-is-off-by-one.patch"

