FILESEXTRAPATHS:append:npcm8xx := "${THISDIR}/${PN}:"

# Fixed ipmid crashing in 64bit system, an alternative solution is still in upstream reviewing
# https://gerrit.openbmc-project.xyz/c/openbmc/phosphor-host-ipmid/+/44260
SRC_URI:append:npcm8xx = " file://0001-phosphor-ipmi-host-Do-not-use-size_t-in-struct-MetaP.patch"

# Fixed pack/unpack compile error at aarch64 platform
SRC_URI:append:npcm8xx = " file://0008-ipmid-message-fix-pack-unpack-compile-error-at-aarch.patch"
