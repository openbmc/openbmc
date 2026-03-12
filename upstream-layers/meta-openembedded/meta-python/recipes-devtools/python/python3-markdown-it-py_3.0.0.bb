SUMMARY = "Python port of markdown-it. Markdown parsing, done right!"
HOMEPAGE = "https://github.com/executablebooks/markdown-it-py"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "e3f60a94fa066dc52ec76661e37c851cb232d92f9886b15cb560aaada2df8feb"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-mdurl"

PYPI_PACKAGE = "markdown-it-py"
CVE_PRODUCT = "markdown-it-py"

BBCLASSEXTEND = "native nativesdk"
