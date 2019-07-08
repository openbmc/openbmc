SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz"
SRC_URI[md5sum] = "04969ad59cc37bddd83741a08b98f350"
SRC_URI[sha256sum] = "b87e608d4d3f7bfdd36ef78d56d53c74e66ab278d318b71e6002a369d36f4873"

UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+).tar"
# json-c releases page is fetching the list of releases in some weird XML format
# from https://s3.amazonaws.com/json-c_releases and processes it with javascript :-/
#UPSTREAM_CHECK_URI = "https://s3.amazonaws.com/json-c_releases/releases/index.html"
RECIPE_UPSTREAM_VERSION = "0.13.1"
RECIPE_UPSTREAM_DATE = "Mar 04, 2018"
CHECK_DATE = "May 02, 2018"

RPROVIDES_${PN} = "libjson"

inherit autotools

EXTRA_OECONF = "--enable-rdrand"

do_configure_prepend() {
    # Clean up autoconf cruft that should not be in the tarball
    rm -f ${S}/config.status
}

BBCLASSEXTEND = "native nativesdk"
