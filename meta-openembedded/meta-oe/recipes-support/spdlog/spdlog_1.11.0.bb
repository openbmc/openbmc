DESCRIPTION = "Very fast, header only, C++ logging library."
HOMEPAGE = "https://github.com/gabime/spdlog/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCREV = "ad0e89cbfb4d0c1ce4d097e134eb7be67baebb36"
SRC_URI = "git://github.com/gabime/spdlog.git;protocol=https;branch=v1.x \
           file://0001-Do-not-use-LFS64-functions-on-linux-musl.patch \
           "

DEPENDS += "fmt"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
# no need to build example & tests & benchmarks on pure yocto
EXTRA_OECMAKE += "-DSPDLOG_INSTALL=on -DSPDLOG_BUILD_SHARED=on -DSPDLOG_BUILD_EXAMPLE=off -DSPDLOG_BUILD_TESTS=off -DSPDLOG_BUILD_BENCH=off -DSPDLOG_FMT_EXTERNAL=on"

inherit cmake
