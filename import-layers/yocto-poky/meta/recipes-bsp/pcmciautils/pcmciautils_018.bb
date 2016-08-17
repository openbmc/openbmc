require pcmciautils.inc

SRC_URI += "file://makefile_fix.patch \
            file://makefile_race.patch \
            file://lex_sys_types.patch \
"

SRC_URI[md5sum] = "885431c3cefb76ffdad8cb985134e996"
SRC_URI[sha256sum] = "57c27be8f04ef4d535bcfa988567316cc57659fe69068327486dab53791e6558"

PR = "r1"

FILES_${PN} += "*/udev */*/udev"
