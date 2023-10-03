SUMMARY = "yaml-cpp parser and emitter library"
DESCRIPTION = "yaml-cpp is a YAML parser and emitter in C++ matching the YAML 1.2 spec."
HOMEPAGE = "https://github.com/jbeder/yaml-cpp"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a8aaf0595c2efc1a9c2e0913e9c1a2c"

# yaml-cpp releases are stored as archive files in github.
# download the exact revision of release
SRC_URI = "git://github.com/jbeder/yaml-cpp.git;branch=master;protocol=https"
SRC_URI += "file://0001-Fix-CMake-export-files-1077.patch"
SRCREV = "0579ae3d976091d7d664aa9d2527e0d0cff25763"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DYAML_CPP_BUILD_TESTS=OFF -DYAML_BUILD_SHARED_LIBS=ON -DYAML_CPP_BUILD_TOOLS=OFF"
