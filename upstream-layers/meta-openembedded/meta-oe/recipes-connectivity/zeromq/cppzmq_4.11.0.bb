DESCRIPTION = "C++ bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db174eaf7b55a34a7c89551197f66e94"
DEPENDS = "zeromq"

SRCREV = "3bcbd9dad2f57180aacd4b4aea292a74f0de7ef4"

SRC_URI = "git://github.com/zeromq/cppzmq.git;branch=master;protocol=https"


inherit cmake

EXTRA_OECMAKE = "-DCPPZMQ_BUILD_TESTS=OFF"

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
