SUMMARY = "A Python Parser"
HOMEPAGE = "https://github.com/davidhalter/parso"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=cbaa2675b2424d771451332a7a69503f"

PYPI_PACKAGE = "parso"

SRC_URI[sha256sum] = "eb3a7b58240fb99099a345571deecc0f9540ea5f4dd2fe14c2a99d6b281ab92d"

inherit setuptools3 pypi

RDEPENDS:${PN} = " \
	python3-crypt \
	python3-difflib \
	python3-logging \
"
