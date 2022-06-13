SUMMARY = "Pika is a RabbitMQ (AMQP 0-9-1) client library for Python."
DESCRIPTION = " \
Pika is a pure-Python implementation of the AMQP 0-9-1 protocol \
including RabbitMQ’s extensions. \
"
SECTION = "devel/python"
HOMEPAGE = "https://pika.readthedocs.io"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=678ec81495ba50edf81e84e4f1aa69f3"

SRC_URI[sha256sum] = "e5fbf3a0a3599f4e114f6e4a7af096f9413a8f24f975c2657ba2fac3c931434f"

inherit pypi setuptools3

PYPI_PACKAGE = "pika"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-tornado \
    ${PYTHON_PN}-twisted \
"
