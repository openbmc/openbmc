SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=c328c862c3335ad464e1c9a3ba574249"

inherit pypi setuptools3

SRC_URI[md5sum] = "ff45a3067658434339da2586a97e526b"
SRC_URI[sha256sum] = "dbd8ebce1c0b10feabb725b321d4a7f9f7353fe16878c8145ca332a173c193c9"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-luma-core \
"
