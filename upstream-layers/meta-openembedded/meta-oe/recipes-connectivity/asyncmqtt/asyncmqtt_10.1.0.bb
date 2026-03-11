SUMMARY = "MQTT communication C++ library using Boost.Asio"
HOMEPAGE = "https://github.com/redboltz/async_mqtt"
LICENSE = "BSL-1.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/redboltz/async_mqtt;protocol=http;branch=main;protocol=https"
SRCREV = "21046ebc4a5475c14cea446715734c88fe33d276"

DEPENDS = "openssl boost"


inherit cmake
