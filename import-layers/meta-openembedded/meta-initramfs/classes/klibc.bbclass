# klcc-cross depends on klibc
DEPENDS =+ "klcc-cross"

# Default for klcc is to build static binaries.
# Set CC = "${TARGET_PREFIX}klcc -shared" to build the dynamic version.

export CC = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS}"
export CC_armv4_linux-gnueabi = "${TARGET_PREFIX}klcc ${TOOLCHAIN_OPTIONS} -march=armv4 -mthumb-interwork"

export CPP = "${CC} -E"

# klcc uses own optimizations by default. See klcc(1) man file.
export CFLAGS=""
export CPPFLAGS=""
export LDFLAGS=""
