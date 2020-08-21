SUMMARY = "Using nlohmann::json with pybind11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e25ff0ec476d06d366439e1120cce98"

SRCREV = "d1d00888bc0eb7c50dde6cff1a5eb4586e620b65"
SRC_URI = "git://github.com/pybind/pybind11_json"

DEPENDS += "nlohmann-json python3-pybind11"

S = "${WORKDIR}/git"

inherit cmake
