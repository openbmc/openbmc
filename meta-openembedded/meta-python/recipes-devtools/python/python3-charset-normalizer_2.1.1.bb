SUMMARY = "The Real First Universal Charset Detector. Open, modern and actively maintained alternative to Chardet."
HOMEPAGE = "https://github.com/ousret/charset_normalizer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0974a390827087287db39928f7c524b5"

SRC_URI[sha256sum] = "5a3d016c7c547f69d6f81fb0db9449ce888b418b5b9952cc5e6e66843e9dd845"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-core \
	${PYTHON_PN}-logging \
	${PYTHON_PN}-codecs \
	${PYTHON_PN}-json \
"
