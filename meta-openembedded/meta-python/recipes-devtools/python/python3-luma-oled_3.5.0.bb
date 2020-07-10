SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=c328c862c3335ad464e1c9a3ba574249"

inherit pypi setuptools3

SRC_URI[md5sum] = "64436aaa7a05a205430b38d2f9a9bec7"
SRC_URI[sha256sum] = "ca62fd7337ee9780b32a8cdc10bcd69879f2cfd97720ab2e17e254a160f6c24f"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-luma-core \
"
