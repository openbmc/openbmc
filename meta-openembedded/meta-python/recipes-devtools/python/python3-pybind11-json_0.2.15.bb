SUMMARY = "Using nlohmann::json with pybind11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e25ff0ec476d06d366439e1120cce98"

SRCREV = "32043f433ed987b2c2ce99d689ec337bcbd4ba95"
SRC_URI = "git://github.com/pybind/pybind11_json;branch=master;protocol=https"

DEPENDS += "nlohmann-json python3-pybind11"

S = "${WORKDIR}/git"

inherit cmake
