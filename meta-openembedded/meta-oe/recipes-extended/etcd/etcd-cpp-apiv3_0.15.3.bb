DESCRIPTION = "C++ API for etcd's v3 client API"
HOMEPAGE = "https://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eae7da6a2cd1788a5cf8a9f838cf6450"

SRC_URI = " \
    git://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3.git;branch=master;protocol=https \
    file://0001-cmake-fix-when-cross-compiling.patch \
"

SRCREV = "e31ac4d4caa55fa662e207150ba40f8151b7ad96"

inherit cmake

DEPENDS += "grpc protobuf cpprest grpc-native protobuf-native"

S = "${WORKDIR}/git"

EXTRA_OECONF += "-DCPPREST_EXCLUDE_WEBSOCKETS=ON"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
