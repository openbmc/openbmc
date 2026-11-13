SUMMARY = "Node.js virtual environment builder"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a9e238ffae5bb6447dbac6291e1dc3a3"


inherit pypi python_setuptools_build_meta
SRC_URI[sha256sum] = "4ea7f88339310afebc3253f2c7a91b94bacf97d73a36a5c108962abdfaca501e"

DEPENDS += "python3-setuptools-scm-native"
