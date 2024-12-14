DESCRIPTION = "Pure-Python HPACK header compression"
HOMEPAGE = "https://github.com/python-hyper/hpack"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5bf1c68e73fbaec2b1687b7e71514393"

SRC_URI[sha256sum] = "fc41de0c63e687ebffde81187a948221294896f6bdc0ae2312708df339430095"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-logging"
