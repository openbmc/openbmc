SUMMARY = "Python HTTP library with thread-safe connection pooling, file post support, sanity friendly, and more"
HOMEPAGE = "https://github.com/shazow/urllib3"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3be3707c5f24a69709682265e29566fe"

SRC_URI[md5sum] = "5e1407428ac33b521c71a7ac273b3847"
SRC_URI[sha256sum] = "dd4fb13a4ce50b18338c7e4d665b21fd38632c5d4b1d9f1a1379276bd3c08d37"

inherit pypi setuptools

RDEPENDS_${PN} += "python-netclient"
