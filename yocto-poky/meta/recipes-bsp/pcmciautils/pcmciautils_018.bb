require pcmciautils.inc

SRC_URI += "file://makefile_fix.patch \
            file://0001-fix-a-parallel-building-issue.patch \
"

SRC_URI[md5sum] = "5d85669b3440baa4532363da6caaf1b4"
SRC_URI[sha256sum] = "79e6ae441278e178c07501d492394ed2c0326fdb66894f6d040ec811b0dc8ed5"

PR = "r1"

FILES_${PN}-dbg += "*/udev/.debug */*/udev/.debug"
FILES_${PN} += "*/udev */*/udev"
