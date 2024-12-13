DESCRIPTION = "A utility library for mocking out the requests Python library."
HOMEPAGE = "https://github.com/getsentry/responses"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e601511a8517f4daf688a8eb95be7a2"

inherit pypi setuptools3

SRC_URI[sha256sum] = "617b9247abd9ae28313d57a75880422d55ec63c29d33d629697590a034358dba"

RDEPENDS:${PN} += " \
	python3-mock \
	python3-pyyaml \
	python3-requests \
	python3-urllib3 \
"
