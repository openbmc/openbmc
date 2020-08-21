SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz"

SRC_URI[sha256sum] = "b8d80a1ddb718b3ba7492916237bbf86609e9709fb007e7f7d4322f02341a4c6"

UPSTREAM_CHECK_URI = "https://github.com/${BPN}/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+)-\d+"

RPROVIDES_${PN} = "libjson"

inherit cmake

BBCLASSEXTEND = "native nativesdk"
