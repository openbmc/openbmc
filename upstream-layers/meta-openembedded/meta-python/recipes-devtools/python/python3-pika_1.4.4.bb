SUMMARY = "Pika is a RabbitMQ (AMQP 0-9-1) client library for Python."
DESCRIPTION = " \
Pika is a pure-Python implementation of the AMQP 0-9-1 protocol \
including RabbitMQ's extensions. \
"
SECTION = "devel/python"
HOMEPAGE = "https://pika.readthedocs.io"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=678ec81495ba50edf81e84e4f1aa69f3"

# The PyPI package omits some files for testing like tests/__init__.py
# so use the GitHub source instead.
SRCREV = "60fee4203a30d5dfa0500857cea6a65ef571d46c"
SRC_URI = "\
    git://github.com/pika/pika;branch=1.4.x;protocol=https;tag=${PV} \
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
