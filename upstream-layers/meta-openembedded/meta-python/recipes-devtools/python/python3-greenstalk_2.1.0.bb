SUMMARY = "A Python 3 client for the beanstalkd work queue"
HOMEPAGE = "https://github.com/mayhewj/greenstalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f98432ba1fce3933c556430fd47298f"

SRC_URI[sha256sum] = "a731ca15bc3b03dfffc438db08c96d0c4e8ce5f472403573e40a1939791c12a7"

RDEPENDS:${PN} += "python3-io python3-core"

inherit pypi python_setuptools_build_meta python_hatchling
