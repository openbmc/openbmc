SUMMARY = "yaml-cpp parser and emitter library"
DESCRIPTION = "yaml-cpp is a YAML parser and emitter in C++ matching the YAML 1.2 spec."
HOMEPAGE = "https://github.com/jbeder/yaml-cpp"
SECTION = "devel"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a8aaf0595c2efc1a9c2e0913e9c1a2c"

# yaml-cpp releases are stored as archive files in github.
# download the exact revision of release
SRC_URI = "git://github.com/jbeder/yaml-cpp.git;branch=master;protocol=https"
SRCREV = "28f93bdec6387d42332220afa9558060c8016795"
PV .= "+git"


inherit cmake

EXTRA_OECMAKE = "-DYAML_CPP_BUILD_TESTS=OFF -DYAML_BUILD_SHARED_LIBS=ON -DYAML_CPP_BUILD_TOOLS=OFF"
