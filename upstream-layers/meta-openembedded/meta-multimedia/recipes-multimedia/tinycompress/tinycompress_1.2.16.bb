DESCRIPTION = "Tinycompress provides a minimal interface to interact \
with compress offload capabilities in ALSA."
HOMEPAGE = "https://github.com/alsa-project/tinycompress"

LICENSE = "BSD-3-Clause OR LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf9105c1a2d4405cbe04bbe3367373a0"

SRCREV = "6b064c5b614bde950560a345ca0702e76668bdab"
SRC_URI = "git://github.com/alsa-project/tinycompress.git;branch=master;protocol=https;tag=v${PV}"

inherit autotools pkgconfig
