DESCRIPTION = "emlog is a Linux kernel module that makes it easy to access the \
most recent (and only the most recent) output from a process"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI = "git://github.com/nicupavel/emlog.git;protocol=http"
SRCREV = "aee53e8dee862f35291242ba41b0ca88010f6c71"

S = "${WORKDIR}/git"

EXTRA_OEMAKE += " \
    CFLAGS='${TARGET_CFLAGS}' \
"
