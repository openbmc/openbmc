SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz \
           file://0001-json_tokener-requires-INF-and-NAN.patch \
          "

SRC_URI[md5sum] = "3ca4bbb881dfc4017e8021b5e0a8c491"
SRC_URI[sha256sum] = "000c01b2b3f82dcb4261751eb71f1b084404fb7d6a282f06074d3c17078b9f3f"

UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+).tar"
# json-c releases page is fetching the list of releases in some weird XML format
# from https://s3.amazonaws.com/json-c_releases and processes it with javascript :-/
#UPSTREAM_CHECK_URI = "https://s3.amazonaws.com/json-c_releases/releases/index.html"
RECIPE_UPSTREAM_VERSION = "0.12"
RECIPE_UPSTREAM_DATE = "Apr 11, 2014"
CHECK_DATE = "Dec 04, 2015"

RPROVIDES_${PN} = "libjson"

inherit autotools

do_configure_prepend() {
    # Clean up autoconf cruft that should not be in the tarball
    rm -f ${S}/config.status
}
