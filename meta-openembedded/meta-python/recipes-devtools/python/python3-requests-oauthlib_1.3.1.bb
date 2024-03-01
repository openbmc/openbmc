LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22d117a849df10d047ed9b792838e863"

SRC_URI[sha256sum] = "75beac4a47881eeb94d5ea5d6ad31ef88856affe2332b9aafb52c6452ccf0d7a"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-requests python3-oauthlib"
