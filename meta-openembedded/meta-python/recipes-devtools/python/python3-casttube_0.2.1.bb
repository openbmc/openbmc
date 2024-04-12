SUMMARY = "YouTube Chromecast API"
HOMEPAGE = "https://github.com/ur1katz/casttube"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3aafde5479a4102b867156e2527a34e"

SRC_URI[sha256sum] = "54d2af8c7949aa9c5db87fb11ef0a478a5d3e7ac6d2d2ac8dd1711e3a516fc82"

inherit pypi setuptools3

FILES:${PN} += "\
    /usr/LICENSE \
"

RDEPENDS:${PN} = "\
    python3-requests \
"
