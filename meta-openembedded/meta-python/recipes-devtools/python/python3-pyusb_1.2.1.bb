SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e64a29fcd3c3dd356a24e235dfcb3905"

DEPENDS += "libusb1 python3-setuptools-scm-native"

RDEPENDS:${PN} += " \
	python3-logging \
"

SRC_URI[sha256sum] = "a4cc7404a203144754164b8b40994e2849fde1cfff06b08492f12fff9d9de7b9"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-ctypes"

BBCLASSEXTEND = "native nativesdk"
