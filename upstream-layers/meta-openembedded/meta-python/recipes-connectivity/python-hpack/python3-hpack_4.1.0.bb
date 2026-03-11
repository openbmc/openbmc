DESCRIPTION = "Pure-Python HPACK header compression"
HOMEPAGE = "https://github.com/python-hyper/hpack"
LICENSE = "MIT"

LIC_FILES_CHKSUM = "file://LICENSE;md5=5bf1c68e73fbaec2b1687b7e71514393"

SRC_URI[sha256sum] = "ec5eca154f7056aa06f196a557655c5b009b382873ac8d1e66e79e87535f1dca"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "python3-logging"
