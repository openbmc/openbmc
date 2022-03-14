SUMMARY = "The Real First Universal Charset Detector. Open, modern and actively maintained alternative to Chardet."
HOMEPAGE = "https://github.com/ousret/charset_normalizer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0974a390827087287db39928f7c524b5"

SRC_URI[sha256sum] = "876d180e9d7432c5d1dfd4c5d26b72f099d503e8fcc0feb7532c9289be60fcbd"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-core \
	${PYTHON_PN}-logging \
	${PYTHON_PN}-codecs \
	${PYTHON_PN}-json \
"
