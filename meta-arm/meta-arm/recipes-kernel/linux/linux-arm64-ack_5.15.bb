# SPDX-License-Identifier: Apache-2.0
#
# Copyright (c) 2022 Arm Limited
#
require linux-arm64-ack.inc

SRC_URI = " \
    git://android.googlesource.com/kernel/common.git;protocol=https;branch=android13-5.15-lts \
    file://0001-lib-build_OID_registry-fix-reproducibility-issues.patch \
    file://0002-vt-conmakehash-improve-reproducibility.patch \
    file://0001-perf-change-root-to-prefix-for-python-install.patch \
    "

# tag: ASB-2022-05-05_13-5.15-93-ge8b3f31d7a60
SRCREV = "e8b3f31d7a60648343ecbd45ab58dbcfc425b22c"
