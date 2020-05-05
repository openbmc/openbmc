require clutter-1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "624dd776a5159de0267587b1df6b97b2"
SRC_URI[archive.sha256sum] = "8b48fac159843f556d0a6be3dbfc6b083fc6d9c58a20a49a6b4919ab4263c4e6"
SRC_URI += "file://install-examples.patch \
            file://run-installed-tests-with-tap-output.patch \
            file://0001-Remove-clutter.types-as-it-is-build-configuration-sp.patch \
            file://run-ptest"
