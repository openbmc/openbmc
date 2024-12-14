SUMMARY = "C++ framework for json-rpc 1.0 and 2.0"
DESCRIPTION = "JsonRpc-Cpp is an OpenSource implementation of JSON-RPC \
               protocol in C++. JSON-RPC is a lightweight remote procedure \
               call protocol similar to XML-RPC."
HOMEPAGE = "https://github.com/cinemast/libjson-rpc-cpp"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4e728c5b36018f6c383b4b9efd9c8840"
SECTION = "libs"

DEPENDS = "curl jsoncpp libmicrohttpd hiredis"

SRC_URI = "git://github.com/cinemast/libjson-rpc-cpp;branch=master;protocol=https"

SRCREV = "ec5ce12fc9c0299e1454cc002c70218b5a6f005b"

S = "${WORKDIR}/git"

PACKAGECONFIG ?= ""
PACKAGECONFIG[coverage] = "-DWITH_COVERAGE=YES,-DWITH_COVERAGE=NO,,"

inherit cmake

EXTRA_OECMAKE += "-DCOMPILE_TESTS=NO -DCOMPILE_STUBGEN=NO -DCOMPILE_EXAMPLES=NO \
                  -DBUILD_SHARED_LIBS=YES -DBUILD_STATIC_LIBS=YES \
                  -DCMAKE_LIBRARY_PATH=${libdir} \
"

do_install:append() {
	sed -i -e 's#${RECIPE_SYSROOT}##g' ${D}${libdir}/libjson-rpc-cpp/cmake/libjson-rpc-cppTargets.cmake
}

FILES:${PN}-dev += "${libdir}/libjson-rpc-cpp/cmake"
