SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e64a29fcd3c3dd356a24e235dfcb3905"

DEPENDS += "libusb1 python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
"

SRC_URI[sha256sum] = "3af070b607467c1c164f49d5b0caabe8ac78dbed9298d703a8dbf9df4052d17e"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-ctypes"

BBCLASSEXTEND = "native nativesdk"
