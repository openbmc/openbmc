require clutter-1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "a03482cbacf735eca8c996f210a21ee5"
SRC_URI[archive.sha256sum] = "e7233314983055e9018f94f56882e29e7fc34d8d35de030789fdcd9b2d0e2e56"
SRC_URI += "file://install-examples.patch \
            file://run-installed-tests-with-tap-output.patch \
            file://0001-Remove-clutter.types-as-it-is-build-configuration-sp.patch \
            file://run-ptest"
