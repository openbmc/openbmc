SUMMARY = "nilfs-utils is a set of utilities for managing the NILFS filesystem."
HOMEPAGE = "https://nilfs.sourceforge.io/"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=385034ac639a62b8415db9814582ee98"
SRC_URI = "git://github.com/nilfs-dev/nilfs-utils.git;protocol=https;branch=v2.2.y;tag=${PV} \
           file://0001-configure.ac-Add-knob-to-define-base-sbindir.patch"

SRCREV = "507064bb3604d0fd88f5e3f6422d9ade73a26120"

DEPENDS = "util-linux util-linux-libuuid"

inherit autotools

# make install is trying to run ldconfig, not suitable for cross builds
EXTRA_OECONF += "LDCONFIG=true --with-base-sbindir=${base_sbindir}"
