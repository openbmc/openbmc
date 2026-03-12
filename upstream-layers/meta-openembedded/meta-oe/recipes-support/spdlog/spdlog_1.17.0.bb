DESCRIPTION = "Very fast, header only, C++ logging library."
HOMEPAGE = "https://github.com/gabime/spdlog/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13886a8d1692948ea9b78edc50e9447c"

SRCREV = "79524ddd08a4ec981b7fea76afd08ee05f83755d"
SRC_URI = "git://github.com/gabime/spdlog.git;protocol=https;branch=v1.x;tag=v${PV}"

DEPENDS += "fmt"


inherit cmake

# no need to build example & tests & benchmarks on pure yocto
EXTRA_OECMAKE += "-DSPDLOG_INSTALL=on -DSPDLOG_BUILD_SHARED=on -DSPDLOG_BUILD_EXAMPLE=off -DSPDLOG_FMT_EXTERNAL=on"

BBCLASSEXTEND = "native"
