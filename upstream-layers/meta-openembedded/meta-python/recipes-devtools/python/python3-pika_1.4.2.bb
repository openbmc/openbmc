SUMMARY = "Pika is a RabbitMQ (AMQP 0-9-1) client library for Python."
DESCRIPTION = " \
Pika is a pure-Python implementation of the AMQP 0-9-1 protocol \
including RabbitMQ's extensions. \
"
SECTION = "devel/python"
HOMEPAGE = "https://pika.readthedocs.io"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=678ec81495ba50edf81e84e4f1aa69f3"

SRC_URI[sha256sum] = "48d1f50297e76be4fc798fd5232d4d532d7a4758e51f7c0ae6c4004b9808a26b"

# The PyPI package omits some files for testing like tests/__init__.py
# so use the GitHub source instead.
SRCREV = "3c9b50d4167e436c73cecae8127e3c5b266b2fce"
SRC_URI = "\
    git://github.com/pika/pika;branch=1.4.x;protocol=https \
    file://run-ptest \
"

inherit python_setuptools_build_meta ptest-python-pytest


DEPENDS += " \
    python3-setuptools-scm-native \
    python3-toml-native \
"

RDEPENDS:${PN} += " \
    python3-logging \
    python3-tornado \
    python3-twisted \
    python3-gevent \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest-timeout \
"

do_install_ptest:append() {
    install -m 0644 ${S}/pyproject.toml ${D}${PTEST_PATH}/
}
