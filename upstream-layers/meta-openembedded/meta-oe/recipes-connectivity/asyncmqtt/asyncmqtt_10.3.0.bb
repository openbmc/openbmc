SUMMARY = "MQTT communication C++ library using Boost.Asio"
HOMEPAGE = "https://github.com/redboltz/async_mqtt"
LICENSE = "BSL-1.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=e4224ccaecb14d942c71d31bef20d78c"

CVE_PRODUCT = "async_mqtt"

SRC_URI = "git://github.com/redboltz/async_mqtt;protocol=http;branch=main;protocol=https;tag=${PV}"
SRCREV = "7129d72c1b9adf159bc506206df3fb422bb9fb84"

DEPENDS = "openssl boost"


inherit cmake
