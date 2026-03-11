SUMMARY = "A pure-Python, bring-your-own-I/O implementation of HTTP/1.1"
HOMEPAGE = "https://github.com/python-hyper/h11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f5501d19c3116f4aaeef89369f458693"

inherit pypi setuptools3

SRC_URI[sha256sum] = "4e35b956cf45792e4caa5885e69fba00bdbc6ffafbfa020300e549b208ee5ff1"

RDEPENDS:${PN} += "python3-profile"
