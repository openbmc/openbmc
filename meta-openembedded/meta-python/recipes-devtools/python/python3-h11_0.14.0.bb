SUMMARY = "A pure-Python, bring-your-own-I/O implementation of HTTP/1.1"
HOMEPAGE = "https://github.com/python-hyper/h11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f5501d19c3116f4aaeef89369f458693"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8f19fbbe99e72420ff35c00b27a34cb9937e902a8b810e2c88300c6f0a3b699d"

RDEPENDS:${PN} += "python3-profile"
