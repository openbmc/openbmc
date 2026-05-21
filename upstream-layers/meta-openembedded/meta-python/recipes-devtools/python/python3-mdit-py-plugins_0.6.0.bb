SUMMARY = "Collection of plugins for markdown-it-py"
HOMEPAGE = "https://github.com/executablebooks/mdit-py-plugins"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "2436f14a7295837ac9228a36feeabda867c4abc488c8d019ad5c0bda88eee040"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-markdown-it-py"

PYPI_PACKAGE = "mdit_py_plugins"

BBCLASSEXTEND = "native nativesdk"
