SUMMARY = "A Python 3 client for the beanstalkd work queue"
HOMEPAGE = "https://github.com/mayhewj/greenstalk"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f98432ba1fce3933c556430fd47298f"

SRC_URI[sha256sum] = "e89b7694ddabbd69562bfe11140fdac1afc9fedcbe2edf6464eaf7c355533ef8"

RDEPENDS:${PN} += "python3-io python3-core"

inherit pypi python_hatchling
