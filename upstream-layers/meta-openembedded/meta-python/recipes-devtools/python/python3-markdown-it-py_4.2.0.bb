SUMMARY = "Python port of markdown-it. Markdown parsing, done right!"
HOMEPAGE = "https://github.com/executablebooks/markdown-it-py"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "04a21681d6fbb623de53f6f364d352309d4094dd4194040a10fd51833e418d49"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-mdurl"

PYPI_PACKAGE = "markdown_it_py"
CVE_PRODUCT = "markdown-it-py"

BBCLASSEXTEND = "native nativesdk"
