# klcc-cross depends on klibc
DEPENDS =+ "klcc-cross"

# Default for klcc is to build static binaries.
# Set CC = "${TARGET_PREFIX}klcc -shared" to build the dynamic version.
CC:forcevariable = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS}"
CC:forcevariable:armv4:linux-gnueabi = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS} -march=armv4 -mthumb-interwork"
CC:append:armv7ve = " ${@' -mfloat-abi=${TUNE_CCARGS_MFLOAT}' if (d.getVar('TUNE_CCARGS_MFLOAT') != '') else ''}"
CC:append:armv7a = " ${@' -mfloat-abi=${TUNE_CCARGS_MFLOAT}' if (d.getVar('TUNE_CCARGS_MFLOAT') != '') else ''}"

# klcc uses own optimizations by default. See klcc(1) man file.
export CFLAGS="${TUNE_CCARGS} ${DEBUG_PREFIX_MAP}"
export CPPFLAGS="${TUNE_CCARGS}"
export LDFLAGS="${TUNE_CCARGS}"

OVERRIDES =. "libc-klibc:"
