SUMMARY = "A fast and thorough lazy object proxy"
HOMEPAGE = "https://python-lazy-object-proxy.readthedocs.io/"
LICENSE = "BSD-2-Clause"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d606e94f56c21c8e0cdde0b622dcdf57"

DEPENDS += "python3-setuptools-scm-native python3-pip-native"

SRC_URI[sha256sum] = "78247b6d45f43a52ef35c25b5581459e85117225408a4128a3daf8bf9648ac69"

inherit pypi setuptools3
