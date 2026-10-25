SUMMARY = "Linux SBC GPIO module"
DESCRIPTION = "Python module which allows control of the GPIO of a Linux SBC."
HOMEPAGE = "https://abyz.me.uk/lg/py_lgpio.html"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61287f92700ec1bdf13bc86d8228cd13"

SRC_URI[sha256sum] = "11372e653b200f76a0b3ef8a23a0735c85ec678a9f8550b9893151ed0f863fff"

SRC_URI += "file://0001-PY_LGPIO-lgpio.i-Replace-undefined-functions.patch"

DEPENDS += "swig-native lg"

RDEPENDS:${PN} += "lg"

inherit pypi setuptools3
