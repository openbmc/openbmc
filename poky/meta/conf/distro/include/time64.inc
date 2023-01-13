GLIBC_64BIT_TIME_FLAGS = " -D_TIME_BITS=64 -D_FILE_OFFSET_BITS=64"

# Only needed for some 32-bit architectures, some relatively newer
# architectures do not need it ( e.g. riscv32 )
TARGET_CC_ARCH:append:arm = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:armeb = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:mipsarcho32 = "${GLIBC_64BIT_TIME_FLAGS}"
TARGET_CC_ARCH:append:powerpc = "${@bb.utils.contains('TUNE_FEATURES', 'm32', '${GLIBC_64BIT_TIME_FLAGS}', '', d)}"
TARGET_CC_ARCH:append:x86 = "${@bb.utils.contains('TUNE_FEATURES', 'm32', '${GLIBC_64BIT_TIME_FLAGS}', '', d)}"

GLIBC_64BIT_TIME_FLAGS:pn-glibc = ""
GLIBC_64BIT_TIME_FLAGS:pn-glibc-tests = ""
# pipewire-v4l2 explicitly sets _FILE_OFFSET_BITS=32 to get access to
# both 32 and 64 bit file APIs.  But it does not handle the time side?
# Needs further investigation
GLIBC_64BIT_TIME_FLAGS:pn-pipewire = ""
# Pulseaudio override certain LFS64 functions e.g. open64 and intentionally
# undefines _FILE_OFFSET_BITS, which wont work when _TIME_BITS=64 is set
GLIBC_64BIT_TIME_FLAGS:pn-pulseaudio = ""
GLIBC_64BIT_TIME_FLAGS:pn-gcc-sanitizers = ""

INSANE_SKIP:libstd-rs[_usr_lib_rustlib_armv7-poky-linux-gnueabihf_lib_libstd.so] = "clock_gettime gettime fcntl fstat64 fstatat64 getsockopt ioctl lstat64 nanosleep prctl recvmsg sendmsg setsockopt stat64"
INSANE_SKIP:librsvg[_usr_bin_rsvg-convert] = "fcntl fstat64 prctl stat64 clock_gettime"
INSANE_SKIP:librsvg[_usr_lib_librsvg-2.so.2.48.0] = "fcntl lstat64 setsockopt sendmsg fstat64 getsockopt ioctl nanosleep timegm fstatat64 prctl mktime gmtime_r recvmsg stat64 clock_gettime localtime_r"

# libpulsedsp.so is a preload-library that hooks libc functions
INSANE_SKIP:pulseaudio[_usr_lib_pulseaudio_libpulsedsp.so] = "setsockopt fcntl"

