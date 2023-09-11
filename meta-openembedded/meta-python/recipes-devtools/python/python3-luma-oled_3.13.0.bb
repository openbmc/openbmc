SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5ec447eb69733e20a55838de7e8cb991"

inherit pypi setuptools3

SRC_URI[sha256sum] = "7e2a0d6a4c968c64980255d681ec27914d9abd5a66a9019b289bf3ad050c2125"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-luma-core \
"
