
RUST_ALTERNATE_EXE_PATH = "${STAGING_LIBDIR_NATIVE}/llvm-rust/bin/llvm-config"

require rust.inc

DEPENDS += "rust-llvm (=${PV})"

inherit cross-canadian

DEPENDS += "  \
            virtual/${HOST_PREFIX}gcc-crosssdk \
            virtual/nativesdk-libc rust-llvm-native \
            virtual/${TARGET_PREFIX}compilerlibs \
            virtual/nativesdk-${HOST_PREFIX}compilerlibs \
            gcc-cross-${TARGET_ARCH} \
           "

# The host tools are likely not to be able to do the necessary operation on
# the target architecturea. Alternatively one could check compatibility
# between host/target.
EXCLUDE_FROM_SHLIBS_${RUSTLIB_TARGET_PN} = "1"

DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                    "

RUST_TARGETGENS = "BUILD HOST TARGET"

INHIBIT_DEFAULT_RUST_DEPS = "1"

export WRAPPER_TARGET_CC = "${CCACHE}${TARGET_PREFIX}gcc --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH} ${SECURITY_NOPIE_CFLAGS}"
export WRAPPER_TARGET_CXX = "${CCACHE}${TARGET_PREFIX}g++ --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH} ${SECURITY_NOPIE_CFLAGS}"
export WRAPPER_TARGET_CCLD = "${TARGET_PREFIX}gcc --sysroot=${STAGING_DIR_TARGET} ${TARGET_CC_ARCH} ${SECURITY_NOPIE_CFLAGS}"
export WRAPPER_TARGET_LDFLAGS = "${TARGET_LDFLAGS}"
export WRAPPER_TARGET_AR = "${TARGET_PREFIX}ar"

python do_configure:prepend() {
    targets = [d.getVar("TARGET_SYS", True), "{}-unknown-linux-gnu".format(d.getVar("HOST_ARCH", True))]
    hosts = ["{}-unknown-linux-gnu".format(d.getVar("HOST_ARCH", True))]
}

INSANE_SKIP:${RUSTLIB_TARGET_PN} = "file-rdeps arch ldflags"
SKIP_FILEDEPS:${RUSTLIB_TARGET_PN} = "1"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

