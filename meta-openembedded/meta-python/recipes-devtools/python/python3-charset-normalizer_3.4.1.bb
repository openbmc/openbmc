SUMMARY = "The Real First Universal Charset Detector. Open, modern and actively maintained alternative to Chardet."
HOMEPAGE = "https://github.com/ousret/charset_normalizer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48178f3fc1374ad7e830412f812bde05"

SRC_URI[sha256sum] = "44251f18cd68a75b56585dd00dae26183e102cd5e0f9f1466e6df5da2ed64ea3"

inherit pypi setuptools3

PYPI_PACKAGE = "charset_normalizer"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
	python3-core \
	python3-logging \
	python3-codecs \
	python3-json \
"

BBCLASSEXTEND = "native nativesdk"
