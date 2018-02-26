SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=50d46bda6248a0b4a46a6d59a66ee08e"
DEPENDS += "libusb1"

SRC_URI[md5sum] = "bc12e83ff3ef1045d4306d13a9955fc1"
SRC_URI[sha256sum] = "14ec66077bdcd6f1aa9e892a0a35a54bb3c1ec56aa740ead64349c18f0186d19"

inherit pypi
