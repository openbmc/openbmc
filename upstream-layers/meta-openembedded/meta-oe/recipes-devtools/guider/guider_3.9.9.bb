SUMMARY = "unified performance analyzer"
HOMEPAGE = "https://github.com/iipeace/guider"
BUGTRACKER = "https://github.com/iipeace/guider/issues"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2c1c00f9d3ed9e24fa69b932b7e7aff2"

PV = "3.9.9"

SRC_URI = "git://github.com/iipeace/${BPN};branch=master;protocol=https;tag=versions/${PV}"
SRCREV = "118ab61773463e56dedc78f5cf6723b97a4ad946"


inherit setuptools3

RDEPENDS:${PN} = "python3 python3-core \
        python3-ctypes python3-shell python3-json"
