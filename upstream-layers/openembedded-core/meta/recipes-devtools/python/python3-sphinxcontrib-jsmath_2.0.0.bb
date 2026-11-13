SUMMARY = "A sphinx extension which renders display math in HTML via JavaScript"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=f0064c10bd544bcffccbc67a41c108d3"

SRC_URI[sha256sum] = "551f780cf9687f9c3c74f0cf9f18aca06e0d6958853453f3803fe4d357c5b924"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
