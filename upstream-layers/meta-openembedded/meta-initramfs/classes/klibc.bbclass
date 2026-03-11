# klcc-cross depends on klibc
DEPENDS =+ "klcc-cross"

# Default for klcc is to build static binaries.
# Set CC = "${TARGET_PREFIX}klcc -shared" to build the dynamic version.
CC:forcevariable = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS}"
CC:forcevariable:armv4:linux-gnueabi = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS} -march=armv4 -mthumb-interwork"
CC:append:armv7ve = " ${@' -mfloat-abi=${TUNE_CCARGS_MFLOAT}' if (d.getVar('TUNE_CCARGS_MFLOAT') != '') else ''}"
CC:append:armv7a = " ${@' -mfloat-abi=${TUNE_CCARGS_MFLOAT}' if (d.getVar('TUNE_CCARGS_MFLOAT') != '') else ''}"

# klcc uses own optimizations by default. See klcc(1) man file.
CFLAGS = "${TUNE_CCARGS} ${DEBUG_PREFIX_MAP}"
CFLAGS[export] = "1"

CPPFLAGS = "${TUNE_CCARGS}"
CPPFLAGS[export] = "1"

LDFLAGS = "${TUNE_CCARGS}"
# Linking with compiler-rt on arm results in
# libclang_rt.builtins-armhf.a(divmoddi4.c.o): in function `__divmoddi4':
#/usr/src/debug/compiler-rt/20.1.8/compiler-rt/lib/builtins/divmoddi4.c:(.text.__divmoddi4+0x7a): undefined reference to `__stack_chk_fail'
LDFLAGS:append:toolchain-clang:libc-klibc:arm = " --rtlib=libgcc --unwindlib=libgcc"
LDFLAGS[export] = "1"

OVERRIDES =. "libc-klibc:"
