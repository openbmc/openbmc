FILESEXTRAPATHS:append:npcm8xx := "${THISDIR}/${PN}:"

# Fixed pack/unpack compile error at aarch64 platform
SRC_URI:append:npcm8xx = " file://0008-ipmid-message-fix-pack-unpack-compile-error-at-aarch.patch"
