DESCRIPTION = "A C-language AMQP client library for use with v2.0+ of the RabbitMQ broker"
HOMEPAGE = "https://github.com/alanxz/rabbitmq-c"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=6b7424f9db80cfb11fdd5c980b583f53"
LICENSE = "MIT"

SRC_URI = "git://github.com/alanxz/rabbitmq-c.git;branch=master;protocol=https"
# v0.11.0-master
SRCREV = "a64c08c68aff34d49a2ac152f04988cd921084f9"

S = "${WORKDIR}/git"

DEPENDS = "popt openssl"

EXTRA_OECMAKE = "-DBUILD_EXAMPLES=OFF -DBUILD_TOOLS=ON -DBUILD_TOOLS_DOCS=OFF -DBUILD_API_DOCS=OFF"

inherit cmake pkgconfig

PACKAGE_BEFORE_PN += "${PN}-tools"
FILES:${PN}-tools = "${bindir}"
