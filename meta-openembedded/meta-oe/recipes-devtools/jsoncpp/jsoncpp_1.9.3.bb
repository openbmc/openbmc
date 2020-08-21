SUMMARY = "JSON C++ lib used to read and write json file."
DESCRIPTION = "Jsoncpp is an implementation of a JSON (http://json.org) reader \
               and writer in C++. JSON (JavaScript Object Notation) is a \
               lightweight data-interchange format. It is easy for humans to \
               read and write. It is easy for machines to parse and generate."

HOMEPAGE = "https://github.com/open-source-parsers/jsoncpp"

SECTION = "libs"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa2a23dd1dc6c139f35105379d76df2b"

PE = "1"

SRCREV = "6aba23f4a8628d599a9ef7fa4811c4ff6e4070e2"
SRC_URI = "git://github.com/open-source-parsers/jsoncpp"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON -DJSONCPP_WITH_TESTS=OFF"

BBCLASSEXTEND = "native"
