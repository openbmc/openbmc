SUMMARY = "A Python 3 client for the beanstalkd work queue"
HOMEPAGE = "https://github.com/mayhewj/greenstalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f98432ba1fce3933c556430fd47298f"

SRC_URI[sha256sum] = "3ebde5fc9ecf986f96f6779fd6d15a53f33d432c52a2e28012e100a99ee154a4"

RDEPENDS:${PN} += "python3-io python3-core"

inherit pypi python_setuptools_build_meta
