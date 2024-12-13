SUMMARY = "Python bytes subclass that decodes hex, with a readable console output."
HOMEPAGE = "https://github.com/ethereum/hexbytes"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16ffc86adf4293d4cfb204e77d62cfe6"

SRC_URI[sha256sum] = "515f00dddf31053db4d0d7636dd16061c1d896c3109b8e751005db4ca46bcca7"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-core \
    python3-email \
    python3-compression \
"
