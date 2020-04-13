SUMMARY = "Light-weight C++ XML Processing Library"
DESCRIPTION = "pugixml is a C++ XML processing library, which consists of a \
DOM-like interface with rich traversal/modification capabilities, \
an extremely fast XML parser which constructs the DOM tree from \
n XML file/buffer, and an XPath 1.0 implementation for complex \
data-driven tree queries."
HOMPAGE = "https://pugixml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://readme.txt;beginline=29;endline=52;md5=d5ee91fb74cbb64223b3693fd64eb169"

SRC_URI = "https://github.com/zeux/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[md5sum] = "f97237e9908201c6d8536210747b66af"
SRC_URI[sha256sum] = "55f399fbb470942410d348584dc953bcaec926415d3462f471ef350f29b5870a"

UPSTREAM_CHECK_URI = "https://github.com/zeux/${BPN}/releases"

inherit cmake

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON \
                  -DCMAKE_BUILD_TYPE=Release \
                  "
