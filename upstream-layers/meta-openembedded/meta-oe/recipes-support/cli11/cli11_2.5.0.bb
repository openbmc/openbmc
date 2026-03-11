SUMMARY = "C++11 command line parser"
DESCRIPTION = "A command line parser for C++11 and beyond that provides a rich feature set with a simple and intuitive interface."
HOMEPAGE = "https://github.com/CLIUtils/CLI11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b8bdde6bda8508bef68a39f3e0d7e939"

DEPENDS = "catch2"

SRCREV = "4160d259d961cd393fd8d67590a8c7d210207348"
SRC_URI = "gitsm://github.com/CLIUtils/CLI11;branch=main;protocol=https"
SRC_URI[sha256sum] = "ea379c4a3cb5799027b1eb451163dff065a3d641aaba23bf4e24ee6b536bd9bc"

inherit cmake

# cli11 is a header only C++ library, so the main package will be empty.
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
