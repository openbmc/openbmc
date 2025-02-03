DEFAULTTUNE ?= "mips32r2-74kc"

MIPSPKGSFX_MIPS16E ??= ""

require conf/machine/include/mips/tune-mips32r2.inc
require conf/machine/include/mips/feature-mips-mips16e.inc


TUNEVALID[74kc] = "Enable MIPS 74K with DSPr2 processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "74kc", " -mtune=74kc", "", d)}"

AVAILTUNES += "mips32r2-74kc mips32r2-74kc-m16"
AVAILTUNES += "mips32r2el-74kc mips32r2el-74kc-m16"

# big endian: kc+dsp2=kc, kc+dsp2+mips16e=kc-m16
TUNE_FEATURES:tune-mips32r2-74kc = "${TUNE_FEATURES:tune-mips32r2-nf} 74kc"
TUNE_FEATURES:tune-mips32r2-74kc-m16 = "${TUNE_FEATURES:tune-mips32r2-74kc} mips16e"

MIPSPKGSFX_VARIANT:tune-mips32r2-74kc = "mips32r2-74kc"
PACKAGE_EXTRA_ARCHS:tune-mips32r2-74kc = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2-nf} mips32r2-74kc-nf"
QEMU_EXTRAOPTIONS:tune-mips32r2-74kc-nf = " -cpu 24Kc"

MIPSPKGSFX_VARIANT:tune-mips32r2-74kc-m16 = "mips32r2-74kc${MIPSPKGSFX_MIPS16E}"
PACKAGE_EXTRA_ARCHS:tune-mips32r2-74kc-m16 = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2-74kc} mips32r2-74kc-m16-nf"
QEMU_EXTRAOPTIONS:tune-mips32r2-74kc-m16-nf = " -cpu 24KEc"

# little endian: kc+dsp2=kc, kc+dsp2+mips16e=kc-m16
TUNE_FEATURES:tune-mips32r2el-74kc = "${TUNE_FEATURES:tune-mips32r2el-nf} 74kc"
TUNE_FEATURES:tune-mips32r2el-74kc-m16 = "${TUNE_FEATURES:tune-mips32r2el-74kc} mips16e"

MIPSPKGSFX_VARIANT:tune-mips32r2el-74kc = "mips32r2el-74kc"
PACKAGE_EXTRA_ARCHS:tune-mips32r2el-74kc = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2el-nf} mips32r2el-74kc-nf"
QEMU_EXTRAOPTIONS:tune-mips32r2el-74kc-nf = " -cpu 24Kc"

MIPSPKGSFX_VARIANT:tune-mips32r2el-74kc-m16 = "mips32r2el-74kc${MIPSPKGSFX_MIPS16E}"
PACKAGE_EXTRA_ARCHS:tune-mips32r2el-74kc-m16 = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2el-74kc} mips32r2el-74kc-m16-nf"
QEMU_EXTRAOPTIONS:tune-mips32r2el-74kc-m16-nf = " -cpu 24KEc"
