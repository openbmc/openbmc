SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=ddfca5d3a55dc20707b094137c913c4c"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "16925fe668f484803df0683add800b19e5dd7316a1d64eb06ec2ae817473901e"

PYPI_PACKAGE = "luma_oled"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
	python3-luma-core \
"
