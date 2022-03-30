SUMMARY = "USB Gadget Configfs Library"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

inherit autotools update-alternatives

PV = "0.1.0"
SRCREV = "a826d136e0e8fa53815f1ba05893e6dd74208c15"
SRC_URI = "git://github.com/libusbg/libusbg.git;branch=master;protocol=https \
           file://0001-Fix-out-of-tree-builds.patch \
          "

S = "${WORKDIR}/git"

ALTERNATIVE:${PN} = "gadget-acm-ecm show-gadgets"
ALTERNATIVE_LINK_NAME[gadget-acm-ecm] = "${bindir}/gadget-acm-ecm"
ALTERNATIVE_LINK_NAME[show-gadgets] = "${bindir}/show-gadgets"
