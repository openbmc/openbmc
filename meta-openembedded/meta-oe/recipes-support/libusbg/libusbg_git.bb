SUMMARY = "USB Gadget Configfs Library"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

inherit autotools

PV = "0.1.0"
SRCREV = "a826d136e0e8fa53815f1ba05893e6dd74208c15"
SRC_URI = "git://github.com/libusbg/libusbg.git \
           file://0001-Fix-out-of-tree-builds.patch \
          "

S = "${WORKDIR}/git"

