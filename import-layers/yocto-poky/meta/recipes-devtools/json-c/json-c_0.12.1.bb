SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "JSON-C implements a reference counting object model that allows you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

SRC_URI = "https://s3.amazonaws.com/json-c_releases/releases/${BP}.tar.gz \
           file://0001-Add-FALLTHRU-comment-to-handle-GCC7-warnings.patch \
           "
SRC_URI[md5sum] = "55f7853f7d8cf664554ce3fa71bf1c7d"
SRC_URI[sha256sum] = "2a136451a7932d80b7d197b10441e26e39428d67b1443ec43bbba824705e1123"

UPSTREAM_CHECK_REGEX = "json-c-(?P<pver>\d+(\.\d+)+).tar"
# json-c releases page is fetching the list of releases in some weird XML format
# from https://s3.amazonaws.com/json-c_releases and processes it with javascript :-/
#UPSTREAM_CHECK_URI = "https://s3.amazonaws.com/json-c_releases/releases/index.html"
RECIPE_UPSTREAM_VERSION = "0.12.1"
RECIPE_UPSTREAM_DATE = "Jun 07, 2016"
CHECK_DATE = "Apr 19, 2017"

RPROVIDES_${PN} = "libjson"

inherit autotools

EXTRA_OECONF = "--enable-rdrand"

do_configure_prepend() {
    # Clean up autoconf cruft that should not be in the tarball
    rm -f ${S}/config.status
}

BBCLASSEXTEND = "native nativesdk"
