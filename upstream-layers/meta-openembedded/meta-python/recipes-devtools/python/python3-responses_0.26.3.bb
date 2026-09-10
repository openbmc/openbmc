DESCRIPTION = "A utility library for mocking out the requests Python library."
HOMEPAGE = "https://github.com/getsentry/responses"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e601511a8517f4daf688a8eb95be7a2"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "b0c11ca8131b8b227b8d5108e6ed39772222bd5aab030ed430e8f99057c4c409"

RDEPENDS:${PN} += " \
	python3-mock \
	python3-pyyaml \
	python3-requests \
	python3-urllib3 \
"
