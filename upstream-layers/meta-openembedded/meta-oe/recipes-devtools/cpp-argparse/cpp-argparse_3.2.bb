SUMMARY = "Argument Parser for Modern C++"
HOMEPAGE = "https://github.com/p-ranav/argparse"
BUGTRACKER = "https://github.com/p-ranav/argparse/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4f3ed9ec2c801700ac8fda1fcd29a330"

SRCREV = "3eda91b2e1ce7d569f84ba295507c4cd8fd96910"
SRC_URI = "git://github.com/p-ranav/argparse.git;branch=master;protocol=https;tag=v${PV}"

inherit cmake
EXTRA_OECMAKE = "-DARGPARSE_BUILD_TESTS=OFF"

BBCLASSEXTEND = "native nativesdk"
