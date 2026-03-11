SUMMARY = "sexpect is another implementation of Expect which is specifically designed for Shell scripts"
HOMEPAGE = "https://github.com/clarkwang/sexpect"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

BRANCH = "master"
SRC_URI = "git://github.com/clarkwang/sexpect;branch=${BRANCH};protocol=https;tag=v${PV}"
SRCREV = "d3645392424d47532be90fcbc0abf3148324785b"


inherit cmake
