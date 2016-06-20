require ccache.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=b3c337e7664559a789d9f7a93e5283c1"

SRCREV = "2254797f1c5cfb83c4272da7b138f7f47218eb7d"

SRC_URI += " \
            file://0002-dev.mk.in-fix-file-name-too-long.patch \
"
