SUMMARY = "Library to enable your code run as a daemon process on Unix-like systems"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13e982bf1b7b164b9d6d1665dac83873"
SRCNAME = "daemonize"

inherit pypi setuptools

RDEPENDS_${PN} = "\
               python-fcntl \
               python-unixadmin \
               python-logging \
               python-resource \
"

SRC_URI[md5sum] = "17bc788a8524b104d4639a68623461e3"
SRC_URI[sha256sum] = "8aa66bad9aa10c682302a4ea9675874191304adeb3239e0776f1ca3041d30619"
