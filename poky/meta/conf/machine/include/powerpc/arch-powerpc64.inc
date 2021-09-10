DEFAULTTUNE ?= "powerpc64"

require conf/machine/include/powerpc/arch-powerpc.inc

TUNEVALID[m64] = "Power ELF64 standard ABI"
TUNECONFLICTS[m64] = "m32 nf"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'm64', ' -m64', '', d)}"
TUNE_ARCH .= "${@bb.utils.contains('TUNE_FEATURES', [ 'm64' ], 'powerpc64${ENDIAN_SFX}', '', d)}"

# musl only supports elfv2 ABI for ppc64
TUNE_CCARGS .= "${@['', ' -mabi=elfv2']['libc-musl' in d.getVar('OVERRIDES').split(':')]}"

AVAILTUNES += "powerpc64 powerpc64le"

TUNE_FEATURES:tune-powerpc64 = "m64 fpu-hard bigendian"
BASE_LIB:tune-powerpc64 = "lib64"
TUNE_PKGARCH:tune-powerpc64 = "powerpc64"
PACKAGE_EXTRA_ARCHS:tune-powerpc64 = "powerpc64"

TUNE_FEATURES:tune-powerpc64le = "m64 fpu-hard"
BASE_LIB:tune-powerpc64le = "lib64"
TUNE_PKGARCH:tune-powerpc64le = "powerpc64le"
PACKAGE_EXTRA_ARCHS:tune-powerpc64le = "powerpc64le"
