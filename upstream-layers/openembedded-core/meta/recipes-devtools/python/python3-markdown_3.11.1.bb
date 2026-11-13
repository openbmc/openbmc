SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ec58cdf7cfed06a21f7a9362627a5480"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "496f4f80f9ebd3395a04c8ec9595c40bbe8ec19e9c67d21fe071a1643e876606"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-logging python3-setuptools"
