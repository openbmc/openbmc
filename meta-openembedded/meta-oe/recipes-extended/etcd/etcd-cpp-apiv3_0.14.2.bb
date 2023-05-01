DESCRIPTION = "C++ API for etcd's v3 client API"
HOMEPAGE = "https://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=eae7da6a2cd1788a5cf8a9f838cf6450"

SRC_URI += "git://github.com/etcd-cpp-apiv3/etcd-cpp-apiv3.git;branch=master;protocol=https"
SRCREV = "91c64e18d325f4b63f0dfbd795c50c9c3ec3d3e0"

inherit cmake

DEPENDS += "grpc protobuf cpprest grpc-native protobuf-native"

S = "${WORKDIR}/git"

EXTRA_OECONF += "-DCPPREST_EXCLUDE_WEBSOCKETS=ON"

INSANE_SKIP:${PN}:append = " ldflags"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
