require postgresql.inc

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=3a9c1120056a102a8c8c4013cd828dce"

PR = "${INC_PR}.0"

SRC_URI += "\
    file://remove.autoconf.version.check.patch \
    file://not-check-libperl.patch \
"

SRC_URI[md5sum] = "a1a2e8014b2b4c49fc58fe2e2fe83681"
SRC_URI[sha256sum] = "4a10640e180e0d9adb587bc25a82dcce6bf507b033637e7fb9d4eeffa33a6b4c"

