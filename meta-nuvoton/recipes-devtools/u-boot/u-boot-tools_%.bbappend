FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:class-native = " file://uboot-mkimage-npcm8xx"

do_install:append:class-native() {
    install -m 0755 ${UNPACKDIR}/uboot-mkimage-npcm8xx ${D}${bindir}/uboot-mkimage-npcm8xx
}
