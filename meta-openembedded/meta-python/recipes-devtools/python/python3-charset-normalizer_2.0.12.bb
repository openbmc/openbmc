SUMMARY = "The Real First Universal Charset Detector. Open, modern and actively maintained alternative to Chardet."
HOMEPAGE = "https://github.com/ousret/charset_normalizer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0974a390827087287db39928f7c524b5"

SRC_URI[sha256sum] = "2857e29ff0d34db842cd7ca3230549d1a697f96ee6d3fb071cfa6c7393832597"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-core \
	${PYTHON_PN}-logging \
	${PYTHON_PN}-codecs \
	${PYTHON_PN}-json \
"
