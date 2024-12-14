SUMMARY = "Cpuset manipulation tool"
HOMEPAGE = "https://github.com/SUSE/cpuset"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/SUSE/cpuset.git;protocol=https;branch=master"

SRCREV = "4f80263208935f0df4f616cf9d8cb7285599a670"
S = "${WORKDIR}/git"

inherit setuptools3

RDEPENDS:${PN} += "python3-core python3-logging python3-pydoc"
