require clutter-1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI[archive.md5sum] = "26494256c980d49703a553916e3083cd"
SRC_URI[archive.sha256sum] = "1d77ce16025f87667a1d00dc4fa617a1935156f63fd17635fdadf138866c9e1e"

SRC_URI += "file://install-examples.patch \
            file://run-installed-tests-with-tap-output.patch \
            file://run-ptest"
