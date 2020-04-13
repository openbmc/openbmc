SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=c328c862c3335ad464e1c9a3ba574249"

inherit pypi setuptools3

SRC_URI[md5sum] = "2944155b2242b9d2ddeb6e139c6083b8"
SRC_URI[sha256sum] = "2ea2b535e7e2f056a51a8c54ad78aa1f00d5699fc439c01bc7c2902823889552"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS_${PN} += " \
	${PYTHON_PN}-luma-core \
"
