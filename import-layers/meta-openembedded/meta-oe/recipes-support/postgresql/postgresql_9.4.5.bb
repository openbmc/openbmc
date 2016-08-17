require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=7d847a9b446ddfe187acfac664189672"

PR = "${INC_PR}.0"

SRC_URI += "\
    file://remove.autoconf.version.check.patch \
    file://not-check-libperl.patch \
"

SRC_URI[md5sum] = "8b2e3472a8dc786649b4d02d02e039a0"
SRC_URI[sha256sum] = "b87c50c66b6ea42a9712b5f6284794fabad0616e6ae420cf0f10523be6d94a39"

