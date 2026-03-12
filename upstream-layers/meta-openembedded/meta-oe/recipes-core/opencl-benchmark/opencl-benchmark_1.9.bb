SUMMARY = "a small OpenCL benchmark program to measure peak GPU/CPU performance"
DESCRIPTION = "A small program, allowing to benchmark OpenCL performans using GPU or CPU drivers."
HOMEPAGE = "https://github.com/ProjectPhysX/OpenCL-Benchmark"

SRC_URI = "git://github.com/ProjectPhysX/OpenCL-Benchmark;protocol=https;branch=master;tag=v${PV}"
SRCREV = "3b669592e21e1deaa025b83953d85e41545dd949"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f7884ffa0d4385c62b5d649de8c4c8da"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opencl"

DEPENDS = "opencl-clhpp virtual/libopencl1"

# There is no Makefile, duplicate what make.sh is doing, while also enabling OE
# build flags and building and linking against normal CL headers / lib
do_compile() {
    ${CXX} ${CPPFLAGS} ${CXXFLAGS} ${LDFLAGS} -std=c++17 -pthread \
        ${S}/src/*.cpp -o ${B}/OpenCL-Benchmark -lOpenCL
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/OpenCL-Benchmark ${D}${bindir}
}
