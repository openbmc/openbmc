SUMMARY = "Petact is a library used for installing and updating compressed tar files"
HOMEPAGE = "https://github.com/matthewscholefield/petact"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;md5=c8533d4ba77519398cdae0173db799a1"

SRC_URI[md5sum] = "47e9a6abc91b4022953e4007ddae9e68"
SRC_URI[sha256sum] = "5dcb0d44f86a601e41a2def9770993cd0ea45c76d37eb3f35e3dd61aa50350e6"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-compression \
    python3-crypt \
    python3-io \
"
