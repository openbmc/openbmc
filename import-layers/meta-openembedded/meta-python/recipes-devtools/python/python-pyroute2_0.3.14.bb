SUMMARY = "A pure Python netlink and Linux network configuration library"
LICENSE = "GPLv2 & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.GPL.v2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://LICENSE.Apache.v2;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "1e7e771702056e61cf522cccc39ea09e"
SRC_URI[sha256sum] = "aed742a7dbe55eb7f02dbb26719f554b9e92198c4b3c7ac501ad03bbb6421962"

SRC_URI += "file://import-simplejson-as-json.patch"

inherit pypi setuptools

RDEPENDS_${PN} += "\
  python-distutils \
  python-simplejson \
  python-multiprocessing \
  python-io python-pprint \
  python-pickle \
  python-logging \
  python-threading \
  python-textutils \
  python-subprocess \
  python-netclient \
"
