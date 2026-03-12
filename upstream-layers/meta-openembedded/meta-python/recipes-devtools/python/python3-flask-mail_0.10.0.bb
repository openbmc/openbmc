SUMMARY = "Flask extension for sending email"
DESCRIPTION = "A Flask extension for sending email"
HOMEPAGE = " https://github.com/rduplain/flask-email"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=201f2b80112efa61b78515bd54e9f138"

SRC_URI[sha256sum] = "44083e7b02bbcce792209c06252f8569dd5a325a7aaa76afe7330422bd97881d"

PYPI_PACKAGE = "flask_mail"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} = " \
    python3-blinker \
    python3-flask \
"
