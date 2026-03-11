SUMMARY = "Collection of plugins for markdown-it-py"
HOMEPAGE = "https://github.com/executablebooks/mdit-py-plugins"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "5f2cd1fdb606ddf152d37ec30e46101a60512bc0e5fa1a7002c36647b09e26b5"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-markdown-it-py"

PYPI_PACKAGE = "mdit_py_plugins"

BBCLASSEXTEND = "native nativesdk"
