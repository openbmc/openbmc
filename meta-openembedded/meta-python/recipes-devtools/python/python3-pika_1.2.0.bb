SUMMARY = "Pika is a RabbitMQ (AMQP 0-9-1) client library for Python."
DESCRIPTION = " \
Pika is a pure-Python implementation of the AMQP 0-9-1 protocol \
including RabbitMQ’s extensions. \
"
SECTION = "devel/python"
HOMEPAGE = "https://pika.readthedocs.io"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=278bdfee5b51616941c1f6b2f1cfcb99"

SRC_URI[sha256sum] = "f023d6ac581086b124190cb3dc81dd581a149d216fa4540ac34f9be1e3970b89"

inherit pypi setuptools3

PYPI_PACKAGE = "pika"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-tornado \
    ${PYTHON_PN}-twisted \
"
