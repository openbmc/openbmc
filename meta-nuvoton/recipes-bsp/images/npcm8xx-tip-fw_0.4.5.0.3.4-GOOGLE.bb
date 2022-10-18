# this recipe is only intended for specific users, so don't choose it by default
DEFAULT_PREFERENCE = "-1"

RELEASE = "TIP_FW_L0_0.4.5_L1_0.3.4_GOOGLE"

SRC_URI[bin.sha256sum] = "bca18be4484d02f2c881e1078aff6cfc323f620e02e3508dcff965aadd841110"

require npcm8xx-tip-fw.inc
