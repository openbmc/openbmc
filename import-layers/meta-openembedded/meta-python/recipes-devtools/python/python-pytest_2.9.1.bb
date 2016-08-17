SUMMARY = "Simple powerful teting with Python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d8a5609608d9a5a180705b0fa298ae01"

SRC_URI[md5sum] = "05165740ea50928e4e971378630163ec"
SRC_URI[sha256sum] = "0d48d27a127644fbe7c8158157e08b35f8255045d4476df694b91eb3a8147e65"

RDEPENDS_${PN} = "python-py"

inherit pypi setuptools
