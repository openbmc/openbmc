SUMMARY = "SSD1306 OLED I2C drive"
DESCRIPTION = "SSD1306 OLED I2C driver working in Linux"
HOMEPAGE = "https://github.com/armlabs/ssd1306_linux"
SECTION = "console/utils"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=7b694e603a996c3bfdc6093ed1f70c8f"

SRC_URI = " \
    git://github.com/armlabs/ssd1306_linux.git;protocol=https;branch=master \
"
SRCREV = "34b42992a43fda69c6ba2557b718601cafe16251"

S = "${WORKDIR}/git"

# coreutils provides fmt which is used in the Makefile
DEPENDS = "i2c-tools coreutils-native"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/ssd1306_bin ${D}${bindir}
}
