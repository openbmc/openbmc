require ${BPN}.inc

LDFLAGS_append_riscv64 = " -pthread"

SRC_URI += " \
            file://0006-make-opencv-configurable.patch \
            file://0007-use-vorbisidec.patch \
            file://0008-fix-luaL-checkint.patch \
"
SRC_URI[md5sum] = "4ff71d262e070fd19f86a1c3542c7b4e"
SRC_URI[sha256sum] = "18c16d4be0f34861d0aa51fbd274fb87f0cab3b7119757ead93f3db3a1f27ed3"
