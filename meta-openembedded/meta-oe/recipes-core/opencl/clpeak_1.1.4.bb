SUMMARY  = "OpenCL synthetic benchmarking tool"
DESCRIPTION = "OpenCL benchmarking tool to measure peak capabilities"

SRC_URI = "git://github.com/krrishnarraj/clpeak.git;protocol=https;branch=master"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "527695de8393a3144863a0a07f9b92f1c734d1c4"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS += "opencl-clhpp virtual/opencl-icd"
