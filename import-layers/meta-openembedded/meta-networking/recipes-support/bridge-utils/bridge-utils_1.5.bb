require bridge-utils.inc

SRC_URI += "file://kernel-headers.patch"

PARALLEL_MAKE = ""

LIC_FILES_CHKSUM = "file://COPYING;md5=f9d20a453221a1b7e32ae84694da2c37"

SRC_URI[md5sum] = "ec7b381160b340648dede58c31bb2238"
SRC_URI[sha256sum] = "42f9e5fb8f6c52e63a98a43b81bd281c227c529f194913e1c51ec48a393b6688"

