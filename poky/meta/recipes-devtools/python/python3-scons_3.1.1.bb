SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=37bb53a08e6beaea0c90e7821d731284"

SRC_URI = "${SOURCEFORGE_MIRROR}/scons/scons-${PV}.tar.gz"
SRC_URI[md5sum] = "35b2a3993313bbedd221d4d5758fd2fd"
SRC_URI[sha256sum] = "4cea417fdd7499a36f407923d03b4b7000b0f9e8fd7b31b316b9ce7eba9143a5"

S = "${WORKDIR}/scons-${PV}"

UPSTREAM_CHECK_URI = "http://scons.org/pages/download.html"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

inherit setuptools3

do_install_prepend() {
    sed -i -e "1s,#!.*python.*,#!${USRBINPATH}/env python3," ${S}/script/*
}

RDEPENDS_${PN} = "\
  python3-core \
  python3-fcntl \
  python3-io \
  python3-json \
  python3-shell \
  python3-pickle \
  python3-pprint \
  "
