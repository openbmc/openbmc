SUMMARY = "File transport adapter for Requests"
HOMEPAGE = "http://github.com/dashea/requests-file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cc728d6087e43796227b0a31422de6b"

SRC_URI[md5sum] = "a907efb75faf0ccbb1857432bf9d8c0f"
SRC_URI[sha256sum] = "f518e7cfe048e053fd1019dfb891b4c55b871c56c5a31693d733240c80b8f191"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    python3-requests \
"

