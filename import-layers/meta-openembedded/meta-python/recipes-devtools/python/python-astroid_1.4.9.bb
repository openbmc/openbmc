SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI[md5sum] = "a57438971de05eb801b82eae59c05217"
SRC_URI[sha256sum] = "a483e7891ce3a06dadfc6cb9095b0938aca58940d43576d72e4502b480c085d7"

inherit setuptools pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-lazy-object-proxy \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-wrapt \
"
