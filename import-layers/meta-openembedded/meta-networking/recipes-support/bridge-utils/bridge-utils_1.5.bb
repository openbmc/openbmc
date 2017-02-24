require bridge-utils.inc

SRC_URI += "\
    file://kernel-headers.patch \
    file://0001-build-error-out-correctly-if-a-submake-fails.patch \
    file://0002-libbridge-fix-some-build-time-warnings-fcntl.h.patch \
    file://0003-bridge-fix-some-build-time-warnings-errno.h.patch \
    file://0004-libbridge-add-missing-include-s-fix-build-against-mu.patch \
    file://0005-build-don-t-ignore-CFLAGS-from-environment.patch \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=f9d20a453221a1b7e32ae84694da2c37"

SRC_URI[md5sum] = "ec7b381160b340648dede58c31bb2238"
SRC_URI[sha256sum] = "42f9e5fb8f6c52e63a98a43b81bd281c227c529f194913e1c51ec48a393b6688"

