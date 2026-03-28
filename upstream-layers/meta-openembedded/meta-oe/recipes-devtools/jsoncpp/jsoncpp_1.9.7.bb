SUMMARY = "JSON C++ lib used to read and write json file."
DESCRIPTION = "Jsoncpp is an implementation of a JSON (http://json.org) reader \
               and writer in C++. JSON (JavaScript Object Notation) is a \
               lightweight data-interchange format. It is easy for humans to \
               read and write. It is easy for machines to parse and generate."

HOMEPAGE = "https://github.com/open-source-parsers/jsoncpp"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d73c165a0f9e86a1342f32d19ec5926"

PE = "1"

SRCREV = "3455302847cf1e4671f1d8f5fa953fd46a7b1404"
SRC_URI = "git://github.com/open-source-parsers/jsoncpp;branch=master;protocol=https;tag=${PV}"


inherit cmake

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DBUILD_OBJECT_LIBS=OFF -DJSONCPP_WITH_TESTS=OFF"

BBCLASSEXTEND = "native nativesdk"
