DESCRIPTION = "A C-language AMQP client library for use with v2.0+ of the RabbitMQ broker"
HOMEPAGE = "https://github.com/alanxz/rabbitmq-c"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=6b7424f9db80cfb11fdd5c980b583f53"
LICENSE = "MIT"

SRC_URI = "git://github.com/alanxz/rabbitmq-c.git"
SRCREV = "4dde30ce8d984edda540349f57eb7995a87ba9de"

S = "${WORKDIR}/git"

DEPENDS = "popt openssl"

EXTRA_OECONF = "--disable-examples --enable-tools --disable-docs"

inherit autotools pkgconfig

PACKAGE_BEFORE_PN += "${PN}-tools"
FILES_${PN}-tools = "${bindir}"
