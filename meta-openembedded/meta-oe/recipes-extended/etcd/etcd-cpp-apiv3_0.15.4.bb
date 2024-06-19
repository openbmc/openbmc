DESCRIPTION = "C++ API for etcd's v3 client API"
HOMEPAGE = "https://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eae7da6a2cd1788a5cf8a9f838cf6450"

SRC_URI = " \
    git://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3.git;branch=master;protocol=https \
    file://0001-include-stdint.h-for-int64_t-types.patch \
"

SRCREV = "ba6216385fc332b23d95683966824c2b86c2474e"

inherit cmake

DEPENDS += "grpc protobuf cpprest grpc-native protobuf-native"

S = "${WORKDIR}/git"

EXTRA_OECONF += "-DCPPREST_EXCLUDE_WEBSOCKETS=ON"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
