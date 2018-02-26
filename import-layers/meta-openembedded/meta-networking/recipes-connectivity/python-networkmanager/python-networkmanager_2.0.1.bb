SUMMARY = "Easy communication with NetworkManager from Python"
HOMEPAGE = "https://github.com/seveas/python-networkmanager"
LICENSE = "Zlib"

LIC_FILES_CHKSUM = "file://COPYING;md5=8d8bac174bf8422b151200e6cc78ebe4"

SRC_URI[md5sum] = "5fc644a65463031295c6b7dd51a0f1bd"
SRC_URI[sha256sum] = "bc36507506ad29bfdac941b0987ebd1cc9633c9a9291d7378e229e4515a0a517"

PYPI_PACKAGE = "python-networkmanager"
inherit pypi setuptools

RDEPENDS_${PN} = "networkmanager python-dbus python-six"
