SUMMARY = "The Real First Universal Charset Detector. Open, modern and actively maintained alternative to Chardet."
HOMEPAGE = "https://github.com/ousret/charset_normalizer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48178f3fc1374ad7e830412f812bde05"

SRC_URI += "file://0001-pyproject.toml-Update-mypy-requirement.patch"

SRC_URI[sha256sum] = "5baececa9ecba31eff645232d59845c07aa030f0c81ee70184a90d35099a0e63"

DEPENDS += "python3-setuptools-scm-native python3-mypy-native"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PYPI_PACKAGE = "charset_normalizer"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
	python3-core \
	python3-logging \
	python3-mypy \
	python3-codecs \
	python3-json \
"

RDEPENDS:${PN}-ptest:append:libc-glibc  = " glibc-charmap-gb18030 glibc-charmaps"
BBCLASSEXTEND = "native nativesdk"
