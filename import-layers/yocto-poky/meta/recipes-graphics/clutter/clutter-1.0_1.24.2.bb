require clutter-1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "3b98e1b33719982a5736ae42cbf7183e"
SRC_URI[archive.sha256sum] = "9631c98cb4bcbfec15e1bbe9eaa6eef0f127201552fce40d7d28f2133803cd63"
SRC_URI += "file://install-examples.patch \
            file://run-installed-tests-with-tap-output.patch \
            file://0001-build-Use-AC_COMPILE_IFELSE.patch \
            file://run-ptest"
