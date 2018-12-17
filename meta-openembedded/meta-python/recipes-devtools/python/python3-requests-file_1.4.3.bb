SUMMARY = "File transport adapter for Requests"
HOMEPAGE = "http://github.com/dashea/requests-file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cc728d6087e43796227b0a31422de6b"

SRC_URI[md5sum] = "470711c9b7e0de1057f7b72a58b7ab51"
SRC_URI[sha256sum] = "8f04aa6201bacda0567e7ac7f677f1499b0fc76b22140c54bc06edf1ba92e2fa"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    python3-requests \
"

