SUMMARY = "Light-weight C++ XML Processing Library"
DESCRIPTION = "pugixml is a C++ XML processing library, which consists of a \
DOM-like interface with rich traversal/modification capabilities, \
an extremely fast XML parser which constructs the DOM tree from \
n XML file/buffer, and an XPath 1.0 implementation for complex \
data-driven tree queries."
HOMEPAGE = "https://pugixml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://readme.txt;beginline=29;endline=52;md5=5dbb98bbc2e5051c26ce32508b4f703e"

SRC_URI = "https://github.com/zeux/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "655ade57fa703fb421c2eb9a0113b5064bddb145d415dd1f88c79353d90d511a"

UPSTREAM_CHECK_URI = "https://github.com/zeux/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_PKGCONFIG=ON \
                  -DBUILD_SHARED_LIBS=ON \
                  -DCMAKE_BUILD_TYPE=Release \
                  "

BBCLASSEXTEND = "native nativesdk"
