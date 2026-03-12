DESCRIPTION = "Tinycompress provides a minimal interface to interact \
with compress offload capabilities in ALSA."
HOMEPAGE = "https://github.com/alsa-project/tinycompress"

LICENSE = "LGPL-2.1-only | BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf9105c1a2d4405cbe04bbe3367373a0"

SRCREV = "ea5c7245beb0b6aec1565cfae0454d6ba374dfdd"
SRC_URI = "git://github.com/alsa-project/tinycompress.git;branch=master;protocol=https;tag=v${PV} \
           file://0001-include-install-compress_ops.h-as-public-header.patch"

inherit autotools pkgconfig
