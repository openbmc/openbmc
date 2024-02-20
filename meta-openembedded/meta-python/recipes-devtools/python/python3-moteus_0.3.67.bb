SUMMARY = "moteus brushless controller library and tools"
HOMEPAGE = "https://github.com/mjbots/moteus"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://setup.py;beginline=3;endline=9;md5=24025d3c660abfc62a83f0e709a45e76"

inherit pypi setuptools3

SRC_URI += "file://0001-lib-python-remove-self-import-from-setup.py.patch"

SRC_URI[sha256sum] = "a2122f20f59b8962057cf8d3fb583e0aa19006eaf2cde49e30027a4d1b8bf925"

S = "${WORKDIR}/moteus-${PV}"

RDEPENDS:${PN} += "\
    python3-can \
    python3-importlib-metadata \
    python3-pyelftools \
    python3-pyserial \
"
