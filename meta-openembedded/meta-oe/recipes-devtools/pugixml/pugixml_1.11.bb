SUMMARY = "Light-weight C++ XML Processing Library"
DESCRIPTION = "pugixml is a C++ XML processing library, which consists of a \
DOM-like interface with rich traversal/modification capabilities, \
an extremely fast XML parser which constructs the DOM tree from \
n XML file/buffer, and an XPath 1.0 implementation for complex \
data-driven tree queries."
HOMEPAGE = "https://pugixml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://readme.txt;beginline=29;endline=52;md5=d5ee91fb74cbb64223b3693fd64eb169"

SRC_URI = "https://github.com/zeux/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "93540f4644fd4e4b02049554ef37fb90"
SRC_URI[sha256sum] = "26913d3e63b9c07431401cf826df17ed832a20d19333d043991e611d23beaa2c"

UPSTREAM_CHECK_URI = "https://github.com/zeux/${BPN}/releases"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_PKGCONFIG=ON \
                  -DBUILD_SHARED_LIBS=ON \
                  -DCMAKE_BUILD_TYPE=Release \
                  "

BBCLASSEXTEND = "native nativesdk"
