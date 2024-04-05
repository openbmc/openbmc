require readline.inc

SRC_URI += "  file://norpath.patch"

SRC_URI += "file://readline82-001.patch;striplevel=0 \
            file://readline82-002.patch;striplevel=0 \
            file://readline82-003.patch;striplevel=0 \
            file://readline82-004.patch;striplevel=0 \
            file://readline82-005.patch;striplevel=0 \
            file://readline82-006.patch;striplevel=0 \
            file://readline82-007.patch;striplevel=0 \
            file://readline82-008.patch;striplevel=0 \
            file://readline82-009.patch;striplevel=0 \
            file://readline82-010.patch;striplevel=0 \
           "

SRC_URI[archive.sha256sum] = "3feb7171f16a84ee82ca18a36d7b9be109a52c04f492a053331d7d1095007c35"
