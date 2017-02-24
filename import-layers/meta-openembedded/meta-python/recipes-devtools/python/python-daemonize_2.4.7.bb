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

SRC_URI[md5sum] = "6759005b12dfeea0d4305f8536b4b0c2"
SRC_URI[sha256sum] = "c0194e861826be456c7c69985825ac7b79632d8ac7ad4cde8e12fee7971468c8"
