SUMMARY = "Cpuset is a Python application to make using the cpusets facilities in the Linux kernel easier"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

S = "${WORKDIR}/git"
SRCREV = "ae629aa1dc959342745ad97406adddf63bb6dbdd"
SRC_URI = "git://github.com/lpechacek/cpuset.git;protocol=https;"

inherit distutils

RDEPENDS_${PN} = "\
    python-core \
    python-re \
    python-logging \
    python-textutils \
    python-unixadmin \
    "
