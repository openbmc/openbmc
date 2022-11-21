SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=20adf60219f02398b350b8bfbdad7a58"

inherit pypi setuptools3

SRC_URI[sha256sum] = "e51c2ce5b88d591f9c64ab49d6bd5abd26759b87180706d615fec796569b6f6b"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-luma-core \
"
