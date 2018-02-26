SUMMARY = "A backport of the subprocess module from Python 3.2 for use on 2.x"
DESCRIPTION = "This is a backport of the subprocess standard library module \
from Python 3.2 - 3.5 for use on Python 2. It includes bugfixes and some new \
features.  On POSIX systems it is guaranteed to be reliable when used in \
threaded applications. It includes timeout support from Python 3.3 and the \
run() API from 3.5 but otherwise matches 3.2's API."
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3e62baa8fb96b11a59d3f2cd335d5c0"

SRC_URI[md5sum] = "824c801e479d3e916879aae3e9c15e16"
SRC_URI[sha256sum] = "1e450a4a4c53bf197ad6402c564b9f7a53539385918ef8f12bdf430a61036590"

inherit pypi setuptools

BBCLASSEXTEND = "native nativesdk"
