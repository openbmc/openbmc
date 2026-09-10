SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ec58cdf7cfed06a21f7a9362627a5480"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "3589362618f743188b4d955b874402bc814f4f83f544dc207719f4baa7d9c45f"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-logging python3-setuptools"
