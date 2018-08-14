DESCRIPTION = "Python-based Network Connectivity Management"
HOMEPAGE = "https://pypi.python.org/pypi/pyconnman/"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "b7fa82034b1c0e1fb1b518ffe3bb4fc0"
SRC_URI[sha256sum] = "46c64c0692063fd0c9fb0216d49f7884bec9fa9760d8473db4b1e2f8162fab4a"

inherit pypi setuptools

RDEPENDS_${PN} = "connman python-dbus python-pprint"
