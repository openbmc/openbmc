SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ddfca5d3a55dc20707b094137c913c4c"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "9e5df928b11c4622872d0da25dd90186ca5e4c1cf1c01b562e7cda86f877f437"

RDEPENDS:${PN} += " \
	python3-luma-core \
"
