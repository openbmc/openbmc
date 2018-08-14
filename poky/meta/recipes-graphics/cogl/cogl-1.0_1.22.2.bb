require cogl-1.0.inc

SRC_URI += "file://test-backface-culling.c-fix-may-be-used-uninitialize.patch \
            file://0001-Fix-an-incorrect-preprocessor-conditional.patch"
SRC_URI[archive.md5sum] = "d53b708ca7c4af03d7254e46945d6b33"
SRC_URI[archive.sha256sum] = "39a718cdb64ea45225a7e94f88dddec1869ab37a21b339ad058a9d898782c00d"

LIC_FILES_CHKSUM = "file://COPYING;md5=1b1a508d91d25ca607c83f92f3e31c84"
