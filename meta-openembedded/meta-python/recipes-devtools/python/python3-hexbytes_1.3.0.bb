SUMMARY = "Python bytes subclass that decodes hex, with a readable console output."
HOMEPAGE = "https://github.com/ethereum/hexbytes"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a26e64020156e806cf0054a6d504b301"

SRC_URI[sha256sum] = "4a61840c24b0909a6534350e2d28ee50159ca1c9e89ce275fd31c110312cf684"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core \
    python3-email \
    python3-compression \
"
