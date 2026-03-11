DESCRIPTION = "Very fast, header only, C++ logging library."
HOMEPAGE = "https://github.com/gabime/spdlog/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9573510928429ad0cbe5ba4de77546e9"

SRCREV = "6fa36017cfd5731d617e1a934f0e5ea9c4445b13"
SRC_URI = "git://github.com/gabime/spdlog.git;protocol=https;branch=v1.x;tag=v${PV}"

DEPENDS += "fmt"


inherit cmake

# no need to build example & tests & benchmarks on pure yocto
EXTRA_OECMAKE += "-DSPDLOG_INSTALL=on -DSPDLOG_BUILD_SHARED=on -DSPDLOG_BUILD_EXAMPLE=off -DSPDLOG_FMT_EXTERNAL=on"

BBCLASSEXTEND = "native"
