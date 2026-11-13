DESCRIPTION = "Python package for creating and manipulating graphs and networks"
HOMEPAGE = "http://networkx.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4ccf59069a708419c8b37163831a4228"

SRC_URI[sha256sum] = "fd77a511bd90f39f3d016351345b52cf5319b813bdca01de3f755d3cca62e96a"

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
