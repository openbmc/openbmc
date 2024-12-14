SUMMARY = "Pure-Python implementation of HSS/LMS Digital Signatures (RFC 8554)"
HOMEPAGE ="https://pypi.org/project/pyhsslms"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bbc59ef8bf238c2902ca816b87b58571"

inherit python_setuptools_build_meta

# Maintainer refused to upload source to pypi.org, but said he would in a
# future release. In the meantime, do github
SRC_URI = "git:///github.com/russhousley/pyhsslms.git;branch=master;protocol=https"
SRCREV = "c798728deed6d3f681c9e6bfd7fe8a6705f5638b"

S = "${WORKDIR}/git"

RDEPENDS:${PN} += " \
    python3-core \
    python3-crypt \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
