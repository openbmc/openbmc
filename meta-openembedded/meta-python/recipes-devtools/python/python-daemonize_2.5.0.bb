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

SRC_URI[md5sum] = "3d64e7a0219ad22e3fca62460f893b6a"
SRC_URI[sha256sum] = "dd026e4ff8d22cb016ed2130bc738b7d4b1da597ef93c074d2adb9e4dea08bc3"
