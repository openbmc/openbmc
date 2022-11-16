FILESEXTRAPATHS:prepend:npcm8xx := "${THISDIR}/${PN}:"

SRC_URI:append:npcm8xx= " file://0001-change-size_t-to-uint32_t-type-in-encrypt_decrypt_da.patch"
