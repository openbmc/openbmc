SUMMARY = "Light-weight C++ XML Processing Library"
DESCRIPTION = "pugixml is a C++ XML processing library, which consists of a \
DOM-like interface with rich traversal/modification capabilities, \
an extremely fast XML parser which constructs the DOM tree from \
n XML file/buffer, and an XPath 1.0 implementation for complex \
data-driven tree queries."
HOMEPAGE = "https://pugixml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://readme.txt;beginline=29;endline=52;md5=cb8b47bf66620c2c9f2ec14f062f4490"

SRC_URI = "https://github.com/zeux/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "4cee1ca4aad395170f4c7a07824f3bdd41f28316c6e1e1090a1425b278ec0b4b"

UPSTREAM_CHECK_URI = "https://github.com/zeux/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_PKGCONFIG=ON \
                  -DBUILD_SHARED_LIBS=ON \
                  -DCMAKE_BUILD_TYPE=Release \
                  "

BBCLASSEXTEND = "native nativesdk"
