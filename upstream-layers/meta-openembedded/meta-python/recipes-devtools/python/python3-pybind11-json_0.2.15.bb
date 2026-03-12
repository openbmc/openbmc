SUMMARY = "Using nlohmann::json with pybind11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0e25ff0ec476d06d366439e1120cce98"

SRCREV = "32043f433ed987b2c2ce99d689ec337bcbd4ba95"
SRC_URI = "git://github.com/pybind/pybind11_json;branch=master;protocol=https \
           file://d72ad4df929bc9d0882298fc1f85ecf589456ff6.patch \
           file://0001-CMakeLists-drop-PYTHON_INCLUDE_DIRS-from-interface.patch"

DEPENDS += "nlohmann-json python3-pybind11"

EXTRA_OECMAKE += "-DPYBIND11_USE_CROSSCOMPILING=ON"

inherit cmake python3native python3targetconfig

BBCLASSEXTEND = "native nativesdk"
