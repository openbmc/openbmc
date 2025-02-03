DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=810f34d8853910e36388f63c0e87f0ed"

SRC_URI[sha256sum] = "307c3669428c5362aab27c8a1260aa8f47c4e91d3891f48be0141738d8d053e1"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
                   python3-decorator \
                   python3-netclient \
                   python3-compression \
                   python3-numbers \
                   python3-pickle \
                   python3-html \
                   python3-xml \
                   python3-json \
                   python3-profile \
                   python3-threading \
                   "

BBCLASSEXTEND = "native nativesdk"
