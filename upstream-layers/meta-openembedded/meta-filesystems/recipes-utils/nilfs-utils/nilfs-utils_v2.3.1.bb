SUMMARY = "nilfs-utils is a set of utilities for managing the NILFS filesystem."
HOMEPAGE = "https://nilfs.sourceforge.io/"

LICENSE = "GPL-2.0-only AND LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=385034ac639a62b8415db9814582ee98"
SRC_URI = "git://github.com/nilfs-dev/nilfs-utils.git;protocol=https;branch=v2.3.y;tag=${PV}"

SRCREV = "805edb2a35178dc1d3d64a553af607fb500ab21d"

DEPENDS = "util-linux util-linux-libuuid"

inherit autotools pkgconfig

# make install is trying to run ldconfig, not suitable for cross builds.
# The UsrMerge hierarchy is autodetected by probing the build host, which is
# wrong when cross compiling, so select it explicitly from DISTRO_FEATURES.
EXTRA_OECONF += "LDCONFIG=true \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '--enable-usrmerge=sbin', '--enable-usrmerge=no', d)}"
