# this recipe is only intended for specific users, so don't choose it by default
DEFAULT_PREFERENCE = "-1"

RELEASE = "TIP_FW_L0_0.4.5_GOOGLE2_L1_0.3.4_GOOGLE2"

SRC_URI[bin.sha256sum] = "b5d53d13bf0a59e62b0e11152df2c4a8e08bdb5adfad98773e33e6117ff4c644"

require npcm8xx-tip-fw.inc
