inherit crosssdk

PN = "gcc-crosssdk-${SDK_SYS}"

SYSTEMHEADERS = "${SDKPATHNATIVE}${prefix_nativesdk}/include"
SYSTEMLIBS = "${SDKPATHNATIVE}${base_libdir_nativesdk}/"
SYSTEMLIBS1 = "${SDKPATHNATIVE}${libdir_nativesdk}/"

GCCMULTILIB = "--disable-multilib"

DEPENDS = "virtual/${TARGET_PREFIX}binutils-crosssdk virtual/nativesdk-${TARGET_PREFIX}libc-for-gcc gettext-native ${NATIVEDEPS}"
PROVIDES = "virtual/${TARGET_PREFIX}gcc-crosssdk virtual/${TARGET_PREFIX}g++-crosssdk"
