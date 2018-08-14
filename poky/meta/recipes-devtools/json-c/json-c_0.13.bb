SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz \
           "
SRC_URI[md5sum] = "11fc5d90c77375e5fc8401e8b9efbf21"
SRC_URI[sha256sum] = "0316780be9ad16c42d7c26b015a784fd5df4b0909fef0aba51cfb13e492ac24d"

UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+).tar"
# json-c releases page is fetching the list of releases in some weird XML format
# from https://s3.amazonaws.com/json-c_releases and processes it with javascript :-/
#UPSTREAM_CHECK_URI = "https://s3.amazonaws.com/json-c_releases/releases/index.html"
RECIPE_UPSTREAM_VERSION = "0.13"
RECIPE_UPSTREAM_DATE = "Dec 07, 2017"
CHECK_DATE = "Jan 31, 2018"

RPROVIDES_${PN} = "libjson"

inherit autotools

EXTRA_OECONF = "--enable-rdrand"

do_configure_prepend() {
    # Clean up autoconf cruft that should not be in the tarball
    rm -f ${S}/config.status
}

BBCLASSEXTEND = "native nativesdk"
