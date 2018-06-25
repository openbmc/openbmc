SUMMARY = "Software Construction tool (make/autotools replacement)"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=46ddf66004e5be5566367cb525a66fc6"

SRC_URI[md5sum] = "b6a292e251b34b82c203b56cfa3968b3"
SRC_URI[sha256sum] = "24475e38d39c19683bc88054524df018fe6949d70fbd4c69e298d39a0269f173"

UPSTREAM_CHECK_URI = "http://scons.org/pages/download.html"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)\.tar"

inherit pypi setuptools

RDEPENDS_${PN} = "\
  python-fcntl \
  python-io \
  python-json \
  python-subprocess \
  python-shell \
  python-pprint \
  "
