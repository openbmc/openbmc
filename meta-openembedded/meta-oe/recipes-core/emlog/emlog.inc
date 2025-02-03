DESCRIPTION = "emlog is a Linux kernel module that makes it easy to access the \
most recent (and only the most recent) output from a process"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/nicupavel/emlog.git;protocol=https;branch=master"
SRC_URI += "file://0001-emlog-Do-not-use-no_llseek-with-kernel-6.12.0.patch"
SRCREV = "a9bbf324fde131ff4cf064e32674086c4ced4dca"
PV = "0.70+git"
S = "${WORKDIR}/git"

CVE_PRODUCT = "nicupavel:emlog"

EXTRA_OEMAKE += " \
    CFLAGS='${TARGET_CFLAGS}' \
"
