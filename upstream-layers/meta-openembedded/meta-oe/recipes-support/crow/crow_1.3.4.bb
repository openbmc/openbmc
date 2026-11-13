SUMMARY = "A Fast and Easy to use microframework for the web"
HOMEPAGE = "https://crowcpp.org/"
DESCRIPTION = "Crow is a C++ framework for creating HTTP or Websocket web services. \
It uses routing similar to Python's Flask which makes it easy to use. \
It is also extremely fast, beating multiple existing C++ frameworks as well as non-C++ frameworks."
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e08502e395a6f7c037ddfe7d2915f58e"

SRC_URI = "git://github.com/CrowCpp/Crow.git;protocol=https;branch=v1.3;tag=v${PV}"
SRCREV = "ae0fef0ee67eec897e401321b99b6dd7cfbdc155"

inherit cmake

PACKAGECONFIG[boost] = "-DCROW_USE_BOOST=ON,-DCROW_USE_BOOST=OFF,boost"

DEPENDS = "${@bb.utils.contains('PACKAGECONFIG', 'boost', '', 'asio', d)}"

EXTRA_OECMAKE = "\
    -DCROW_BUILD_EXAMPLES=OFF \
    -DCROW_BUILD_TESTS=OFF \
"
