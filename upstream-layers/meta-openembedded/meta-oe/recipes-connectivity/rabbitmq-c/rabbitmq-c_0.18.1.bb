DESCRIPTION = "A C-language AMQP client library for use with v2.0+ of the RabbitMQ broker"
HOMEPAGE = "https://github.com/alanxz/rabbitmq-c"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7e12f6e40e662e039e2f02b4893011ec"
LICENSE = "MIT"

SRC_URI = "git://github.com/alanxz/rabbitmq-c.git;branch=master;protocol=https;tag=v${PV} \
"
SRCREV = "dc22a7fa21522788310ec7f3f49bc3f028150b22"


DEPENDS = "popt openssl"

EXTRA_OECMAKE = "-DBUILD_EXAMPLES=OFF -DBUILD_TOOLS=ON -DBUILD_TOOLS_DOCS=OFF -DBUILD_API_DOCS=OFF"

inherit cmake pkgconfig

PACKAGE_BEFORE_PN += "${PN}-tools"
FILES:${PN}-tools = "${bindir}"
