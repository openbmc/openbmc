SUMMARY = "Collection of plugins for markdown-it-py"
HOMEPAGE = "https://github.com/executablebooks/mdit-py-plugins"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "f4918cb50119f50446560513a8e311d574ff6aaed72606ddae6d35716fe809c6"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-markdown-it-py"

PYPI_PACKAGE = "mdit_py_plugins"

BBCLASSEXTEND = "native nativesdk"
