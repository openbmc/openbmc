DESCRIPTION = "Very fast, header only, C++ logging library."
HOMEPAGE = "https://github.com/gabime/spdlog/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRCREV = "eb3220622e73a4889eee355ffa37972b3cac3df5"
SRC_URI = "git://github.com/gabime/spdlog.git;protocol=https;branch=v1.x; \
           file://0001-Enable-use-of-external-fmt-library.patch"

DEPENDS += "fmt"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
# no need to build example & tests & benchmarks on pure yocto
EXTRA_OECMAKE += "-DSPDLOG_INSTALL=on -DSPDLOG_BUILD_SHARED=on -DSPDLOG_BUILD_EXAMPLE=off -DSPDLOG_BUILD_TESTS=off -DSPDLOG_BUILD_BENCH=off -DSPDLOG_FMT_EXTERNAL=on"

inherit cmake
