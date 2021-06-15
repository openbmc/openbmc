SUMMARY = "Easy communication with NetworkManager from Python"
HOMEPAGE = "https://github.com/seveas/python-networkmanager"
LICENSE = "Zlib"

LIC_FILES_CHKSUM = "file://COPYING;md5=9f8a5b5844f027357ab19ef3e3c6a956"

SRC_URI[md5sum] = "7e05d3ca40a4fa50222ba93cda3d0d02"
SRC_URI[sha256sum] = "de6eb921d94aba7549f428ed2b3aa482a5d543ecb6965cbaa0fbb555ab31b9d5"

PYPI_PACKAGE = "python-networkmanager"
inherit pypi setuptools3

RDEPENDS_${PN} = "networkmanager python3-dbus python3-six"
