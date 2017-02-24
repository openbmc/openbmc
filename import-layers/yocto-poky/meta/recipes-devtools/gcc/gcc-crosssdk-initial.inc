inherit crosssdk

PN = "gcc-crosssdk-initial-${SDK_SYS}"

SYSTEMHEADERS = "${SDKPATHNATIVE}${prefix_nativesdk}/include"
SYSTEMLIBS = "${SDKPATHNATIVE}${base_libdir_nativesdk}/"
SYSTEMLIBS1 = "${SDKPATHNATIVE}${libdir_nativesdk}/"

DEPENDS = "virtual/${TARGET_PREFIX}binutils-crosssdk gettext-native ${NATIVEDEPS}"
PROVIDES = "virtual/${TARGET_PREFIX}gcc-initial-crosssdk"
