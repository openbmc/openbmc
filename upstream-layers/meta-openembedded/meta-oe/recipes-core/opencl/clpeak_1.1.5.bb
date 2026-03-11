SUMMARY  = "OpenCL synthetic benchmarking tool"
DESCRIPTION = "OpenCL benchmarking tool to measure peak capabilities"

SRC_URI = "git://github.com/krrishnarraj/clpeak.git;protocol=https;branch=master"

LICENSE  = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "b2e647ffb8f42aa22ce4b0194d6ef6d16d5002b0"


inherit cmake

DEPENDS += "opencl-clhpp virtual/opencl-icd"
