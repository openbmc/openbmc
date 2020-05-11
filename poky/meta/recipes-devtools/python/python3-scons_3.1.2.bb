SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE-python3-scons-${PV};md5=e14e1b33428df24a40a782ae142785d0"

# pypi package does not have a valid license file
SRC_URI += "https://raw.githubusercontent.com/SCons/scons/${PV}/LICENSE;downloadfilename=LICENSE-python3-scons-${PV};name=license"

SRC_URI[md5sum] = "f9c4ad06dcf1427be95472eaf380c81a"
SRC_URI[sha256sum] = "8aaa483c303efeb678e6f7c776c8444a482f8ddc3ad891f8b6cdd35264da9a1f"
SRC_URI[license.md5sum] = "e14e1b33428df24a40a782ae142785d0"
SRC_URI[license.sha256sum] = "72ed889165fb28378cadac14552be4a959f1ebab6b148abb5dd2b49712c3c6f6"

S = "${WORKDIR}/scons-${PV}"

UPSTREAM_CHECK_URI = "http://scons.org/pages/download.html"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

inherit pypi setuptools3

do_install_prepend() {
    sed -i -e "1s,#!.*python.*,#!${USRBINPATH}/env python3," ${S}/script/*
}

RDEPENDS_${PN}_class-target = "\
  python3-core \
  python3-fcntl \
  python3-io \
  python3-json \
  python3-shell \
  python3-pickle \
  python3-pprint \
  "
