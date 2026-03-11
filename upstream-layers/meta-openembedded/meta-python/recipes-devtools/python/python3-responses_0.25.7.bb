DESCRIPTION = "A utility library for mocking out the requests Python library."
HOMEPAGE = "https://github.com/getsentry/responses"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e601511a8517f4daf688a8eb95be7a2"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "8ebae11405d7a5df79ab6fd54277f6f2bc29b2d002d0dd2d5c632594d1ddcedb"

RDEPENDS:${PN} += " \
	python3-mock \
	python3-pyyaml \
	python3-requests \
	python3-urllib3 \
"
