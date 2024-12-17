require u-boot-common-aspeed_${PV}.inc
require u-boot-aspeed.inc

PROVIDES += "u-boot"
DEPENDS += "dtc-native"

# Squash TMPDIR warning due to old u-boot.
ERROR_QA:remove = "buildpaths"
WARNING_QA:append = "buildpaths"

# FIXME this can/should be removed when we have rebased off our
# 2016 snapshot onto a more modern (circa late 2018) u-boot tree.
BUILD_CFLAGS:remove = "-isystem${STAGING_INCDIR_NATIVE}"
