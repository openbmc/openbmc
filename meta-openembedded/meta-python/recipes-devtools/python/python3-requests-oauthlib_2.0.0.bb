LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22d117a849df10d047ed9b792838e863"

SRC_URI[sha256sum] = "b3dffaebd884d8cd778494369603a9e7b58d29111bf6b41bdc2dcd87203af4e9"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-requests python3-oauthlib"
