SUMMARY = "Yet Another V4L2 Test Application"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "git://git.ideasonboard.org/yavta.git;branch=master;protocol=https \
          "
SRCREV = "3bf1c3bfc25a780dda8333653b80932cb1e422a8"

PV = "0.0"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

inherit meson
