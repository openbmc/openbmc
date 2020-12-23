SUMMARY = "A Python 3 client for the beanstalkd work queue"
HOMEPAGE = "https://github.com/mayhewj/greenstalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f98432ba1fce3933c556430fd47298f"

SRC_URI[md5sum] = "3374649586a8016fecaf0ce5ecf9985d"
SRC_URI[sha256sum] = "0c9f2af79ac8ea526891ae8d7e9500341a0f657465a541d6eaedb35ff70f4fe3"

RDEPENDS_${PN} += "python3-io python3-core"

inherit pypi
inherit setuptools3
