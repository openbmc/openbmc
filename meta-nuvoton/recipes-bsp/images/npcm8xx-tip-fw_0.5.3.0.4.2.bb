SRCREV = "e9bc9838f49402c185bf987dab362e5a938cdd5a"

OUTPUT_BIN = "output_binaries_${DEVICE_GEN}_${IGPS_MACHINE}"

SRC_URI[output_binaries_A1_EB.sha256sum] = "5ea2f796ee2a917700f9a8d0e2bc7a2fa6c540573df81956cb9a73aba3bfd2e0"
SRC_URI[output_binaries_A1_Google.sha256sum] = "c07d740093b0599e76463456c5b0a2325a9da9fa99cae2f7a349a68f837e60d1"
SRC_URI[output_binaries_A1_SVB.sha256sum] = "57af59c6e2cfb7cc91865aa9fc434ac57fa39ecee51cb63c07d0ba56bc21526f"
SRC_URI[output_binaries_Z1_EB.sha256sum] = "9ab9e17f7502fbe5d7a17a4e4f29614f369c799ba2896e8428b31bd4a2a765e0"
SRC_URI[output_binaries_Z1_SVB.sha256sum] = "3c23d41421706f0f14db239b474811bdff9d39564c6a618304ab56bcd706550c"

require npcm8xx-tip-fw.inc
