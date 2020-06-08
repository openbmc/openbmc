SUMMARY = "Easy communication with NetworkManager from Python"
HOMEPAGE = "https://github.com/seveas/python-networkmanager"
LICENSE = "Zlib"

LIC_FILES_CHKSUM = "file://COPYING;md5=8d8bac174bf8422b151200e6cc78ebe4"

SRC_URI[md5sum] = "f638d854a3639fb37c0e06d1092a771e"
SRC_URI[sha256sum] = "aef1e34d98d7bec7cc368e0ca0f2e97493f9b5ebe6d7103f8f6460cfca3dc6fc"

PYPI_PACKAGE = "python-networkmanager"
inherit pypi setuptools3

RDEPENDS_${PN} = "networkmanager python3-dbus python3-six"
