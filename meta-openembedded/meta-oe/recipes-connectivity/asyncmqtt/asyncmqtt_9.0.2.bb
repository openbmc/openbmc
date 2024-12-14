SUMMARY = "MQTT communication C++ library using Boost.Asio"
HOMEPAGE = "https://github.com/redboltz/async_mqtt"
LICENSE = "BSL-1.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/redboltz/async_mqtt;protocol=http;branch=main;protocol=https"
SRCREV = "51a97adfd022cf5205c3bb31f38ace1f7fbd21a1"

DEPENDS = "openssl boost"

S = "${WORKDIR}/git"

inherit cmake
