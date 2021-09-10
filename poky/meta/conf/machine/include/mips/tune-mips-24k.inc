DEFAULTTUNE ?= "mips32r2-24kc"

MIPSPKGSFX_MIPS16E ??= ""

require conf/machine/include/mips/tune-mips32r2.inc
require conf/machine/include/mips/feature-mips-mips16e.inc


TUNEVALID[24kc] = "Enable MIPS 24Kc processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "24kc", " -mtune=24kc", "", d)}"
TUNEVALID[24kec] = "Enable MIPS 24KEc processor optimizations, including DSP"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "24kec", " -mtune=24kec -mdsp", "", d)}"


AVAILTUNES += "mips32r2-24kc   mips32r2-24kec   mips32r2-24kec-m16"
AVAILTUNES += "mips32r2el-24kc mips32r2el-24kec mips32r2el-24kec-m16"

# big endian: kc, kc+dsp=kec, kc+dsp+mips16e=kec-m16
TUNE_FEATURES:tune-mips32r2-24kc = "${TUNE_FEATURES:tune-mips32r2-nf} 24kc"
TUNE_FEATURES:tune-mips32r2-24kec = "${TUNE_FEATURES:tune-mips32r2-nf} 24kec"
TUNE_FEATURES:tune-mips32r2-24kec-m16 = "${TUNE_FEATURES:tune-mips32r2-24kec} mips16e"

MIPSPKGSFX_VARIANT:tune-mips32r2-24kc = "mips32r2-24kc"
PACKAGE_EXTRA_ARCHS:tune-mips32r2-24kc = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2-nf} mips32r2-24kc-nf"
QEMU_EXTRAOPTIONS_mips32r2-24kc-nf = " -cpu 24Kc"

MIPSPKGSFX_VARIANT:tune-mips32r2-24kec = "mips32r2-24kec"
PACKAGE_EXTRA_ARCHS:tune-mips32r2-24kec = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2-24kc} mips32r2-24kec-nf"
QEMU_EXTRAOPTIONS_mips32r2-24kec-nf = " -cpu 24KEc"

MIPSPKGSFX_VARIANT:tune-mips32r2-24kec-m16 = "mips32r2-24kec${MIPSPKGSFX_MIPS16E}"
PACKAGE_EXTRA_ARCHS:tune-mips32r2-24kec-m16 = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2-24kec} mips32r2-24kec-m16-nf"
QEMU_EXTRAOPTIONS_mips32r2-24kec-m16-nf = " -cpu 24KEc"


# little endian: kc, kc+dsp=kec, kc+dsp+mips16e=kec-m16
TUNE_FEATURES:tune-mips32r2el-24kc = "${TUNE_FEATURES:tune-mips32r2el-nf} 24kc"
TUNE_FEATURES:tune-mips32r2el-24kec = "${TUNE_FEATURES:tune-mips32r2el-nf} 24kec"
TUNE_FEATURES:tune-mips32r2el-24kec-m16 = "${TUNE_FEATURES:tune-mips32r2el-24kec} mips16e"

MIPSPKGSFX_VARIANT:tune-mips32r2el-24kc = "mips32r2el-24kc"
PACKAGE_EXTRA_ARCHS:tune-mips32r2el-24kc = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2el-nf} mips32r2el-24kc-nf"
QEMU_EXTRAOPTIONS_mips32r2el-24kc-nf = " -cpu 24Kc"

MIPSPKGSFX_VARIANT:tune-mips32r2el-24kec = "mips32r2el-24kec"
PACKAGE_EXTRA_ARCHS:tune-mips32r2el-24kec = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2el-24kc} mips32r2el-24kec-nf"
QEMU_EXTRAOPTIONS_mips32r2el-24kec-nf = " -cpu 24KEc"

MIPSPKGSFX_VARIANT:tune-mips32r2el-24kec-m16 = "mips32r2el-24kec${MIPSPKGSFX_MIPS16E}"
PACKAGE_EXTRA_ARCHS:tune-mips32r2el-24kec-m16 = "${PACKAGE_EXTRA_ARCHS:tune-mips32r2el-24kec} mips32r2el-24kec-m16-nf"
QEMU_EXTRAOPTIONS_mips32r2el-24kec-m16-nf = " -cpu 24KEc"
