SUMMARY = "HTML parser based on the WHATWG HTML specifcation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ba5ada9e6fead1fdc32f43c9f10ba7c"

SRC_URI[sha256sum] = "b2e5b40261e20f354d198eae92afc10d750afb487ed5e50f9c4eaf07c184146f"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-lxml \
    python3-six \
    python3-webencodings \
    python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
