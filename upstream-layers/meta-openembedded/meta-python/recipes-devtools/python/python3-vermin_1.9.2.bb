SUMMARY = "Concurrently detect the minimum Python versions needed to run code"
HOMEPAGE = "https://github.com/netromdk/vermin"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0303dc5537dcc0e24a5b259af6fc0bf4"

SRC_URI[sha256sum] = "ba4e01ab817c6baadf5f057bd6ea691e1147bcaf00c1ac8db54df0e25c6d2c6d"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"
