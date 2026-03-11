SUMMARY = "AttrDict is an MIT-licensed library that provides mapping objects that allow their elements to be accessed both as keys and as attributes"
HOMEPAGE = "https://pypi.org/project/attrdict3/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2b37be7e71ebfc92a94bfacf6b20a1cc"

DEPENDS = ""

SRC_URI[sha256sum] = "004c171ca1120cc1755701db99d7fa4944afb1e68950434efdaa542513335fe8"

inherit pypi setuptools3

BBCLASSEXTEND = "native"

RDEPENDS:${PN} += "python3-six"
