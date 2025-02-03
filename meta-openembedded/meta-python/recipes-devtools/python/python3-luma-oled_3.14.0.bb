SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=95cba8b3a40c6f55d5d901980fe6e067"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "36218565eda0614c8cf44ef42cb9a5904ddf808e4516e99ddae111fc93c5a206"

PYPI_PACKAGE = "luma_oled"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
	python3-luma-core \
"
