SUMMARY = "Collection of plugins for markdown-it-py"
HOMEPAGE = "https://github.com/executablebooks/mdit-py-plugins"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "a2bca0f039f39dbd35fb74ae1b5f998608c437463371f0ff7f49a19a17a114d0"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-markdown-it-py"

PYPI_PACKAGE = "mdit_py_plugins"

BBCLASSEXTEND = "native nativesdk"
