SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "https://avro.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d502b41f76179fc84e536236f359cae"

DEPENDS = "jansson zlib xz"

BRANCH = "branch-1.11"
SRCREV = "35ff8b997738e4d983871902d47bfb67b3250734"
SRC_URI = "git://github.com/apache/avro;branch=${BRANCH};protocol=https \
          "

S = "${WORKDIR}/git/lang/c"

inherit cmake pkgconfig

# http://errors.yoctoproject.org/Errors/Details/766902/
# avro-c/1.11.3/git/lang/c/examples/quickstop.c:123:61: error: passing argument 3 of 'first_value.iface->get_string' from incompatible pointer type [-Wincompatible-pointer-types]
# avro-c/1.11.3/git/lang/c/examples/quickstop.c:127:60: error: passing argument 3 of 'last_value.iface->get_string' from incompatible pointer type [-Wincompatible-pointer-types]
# avro-c/1.11.3/git/lang/c/examples/quickstop.c:131:61: error: passing argument 3 of 'phone_value.iface->get_string' from incompatible pointer type [-Wincompatible-pointer-types]
CFLAGS += "-Wno-error=incompatible-pointer-types"
