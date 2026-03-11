SUMMARY = "Python bytes subclass that decodes hex, with a readable console output."
HOMEPAGE = "https://github.com/ethereum/hexbytes"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a26e64020156e806cf0054a6d504b301"

SRC_URI[sha256sum] = "a657eebebdfe27254336f98d8af6e2236f3f83aed164b87466b6cf6c5f5a4765"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core \
    python3-email \
    python3-compression \
"
