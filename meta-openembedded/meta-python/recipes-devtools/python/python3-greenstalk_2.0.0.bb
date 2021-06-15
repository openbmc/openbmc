SUMMARY = "A Python 3 client for the beanstalkd work queue"
HOMEPAGE = "https://github.com/mayhewj/greenstalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f98432ba1fce3933c556430fd47298f"

SRC_URI[sha256sum] = "0020970abdb6f400586938573cbbec80410e83805d61e46cf76ea3ed71129257"

RDEPENDS_${PN} += "python3-io python3-core"

inherit pypi
inherit setuptools3
