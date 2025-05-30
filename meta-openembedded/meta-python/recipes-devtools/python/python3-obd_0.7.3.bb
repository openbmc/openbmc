DESCRIPTION = "A python module for handling realtime sensor data from OBD-II vehicle ports"HOMEPAGE = "https://github.com/brendan-w/python-OBD"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://README.md;md5=176d0f9ad45c10895296260d426fa862"

SRC_URI[sha256sum] = "27b8f043376ca700edb98bf5216e2912295ecde0e735b260999f2d9ddf342522"

inherit python_setuptools_build_meta pypi

RDEPENDS:${PN} += "python3-pyserial python3-pint python3-setuptools python3-packaging"
