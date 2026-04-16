SUMMARY = "A Fast and Easy to use microframework for the web"
HOMEPAGE = "https://crowcpp.org/"
DESCRIPTION = "Crow is a C++ framework for creating HTTP or Websocket web services. \
It uses routing similar to Python's Flask which makes it easy to use. \
It is also extremely fast, beating multiple existing C++ frameworks as well as non-C++ frameworks."
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e08502e395a6f7c037ddfe7d2915f58e"

SRC_URI = "git://github.com/CrowCpp/Crow.git;protocol=https;branch=v1.3;tag=v${PV}"
SRCREV = "f8c060c51feeca2c65828fb6f538603db4392d55"

inherit cmake

DEPENDS = "asio"

EXTRA_OECMAKE = "\
    -DCROW_BUILD_EXAMPLES=OFF \
    -DCROW_BUILD_TESTS=OFF \
"
