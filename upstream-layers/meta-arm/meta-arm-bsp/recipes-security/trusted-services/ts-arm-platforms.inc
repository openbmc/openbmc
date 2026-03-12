FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRC_URI:append:corstone1000  = " \
    file://0001-Revert-Load-and-store-UEFI-variable-index-in-chunks.patch \
    file://0002-Remove-PLATFORM_HAS_ATTEST_PK-define-from-IAT-test.patch \
"

# The patches above introduce errors with GCC 14.1, silence them for now
CFLAGS:append:corstone1000 = " -Wno-int-conversion -Wno-implicit-function-declaration"

COMPATIBLE_MACHINE:fvp-base = "fvp-base"
TS_PLATFORM:fvp-base = "arm/fvp/fvp_base_revc-2xaemv8a"

EXTRA_OECMAKE:append:corstone1000 = " \
    -DCORSTONE_1000_TYPE=${CORSTONE_1000_TYPE} \
    -DCMAKE_COMPILE_WARNING_AS_ERROR=OFF \
"
