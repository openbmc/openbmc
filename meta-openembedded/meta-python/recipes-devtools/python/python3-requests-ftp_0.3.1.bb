SUMMARY = "FTP Transport Adapter for Requests"
HOMEPAGE = "http://github.com/Lukasa/requests-ftp"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6683a23c9968b97709441dc884d46df6"

SRC_URI[sha256sum] = "7504ceb5cba8a5c0135ed738596820a78c5f2be92d79b29f96ba99b183d8057a"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-requests \
"

