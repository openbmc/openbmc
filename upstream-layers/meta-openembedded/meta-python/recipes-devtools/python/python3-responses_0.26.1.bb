DESCRIPTION = "A utility library for mocking out the requests Python library."
HOMEPAGE = "https://github.com/getsentry/responses"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e601511a8517f4daf688a8eb95be7a2"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "2eb3218553cc8f79b57d257bac23af5e1bf381f5b9390b1767816f0843e01dc2"

RDEPENDS:${PN} += " \
	python3-mock \
	python3-pyyaml \
	python3-requests \
	python3-urllib3 \
"
