require clutter-1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "b065e9ca53d1f6bc1ec26aeb27338bb7"
SRC_URI[archive.sha256sum] = "67514e7824b3feb4723164084b36d6ce1ae41cb3a9897e9f1a56c8334993ce06"
SRC_URI += "file://install-examples.patch \
            file://run-installed-tests-with-tap-output.patch \
            file://0001-Remove-clutter.types-as-it-is-build-configuration-sp.patch \
            file://run-ptest"
