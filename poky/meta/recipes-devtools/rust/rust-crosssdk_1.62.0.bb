require rust.inc
inherit crosssdk
require rust-cross.inc
require rust-source.inc

DEPENDS += "virtual/${TARGET_PREFIX}gcc-crosssdk virtual/nativesdk-${TARGET_PREFIX}compilerlibs virtual/nativesdk-libc"
PROVIDES = "virtual/nativesdk-${TARGET_PREFIX}rust"
PN = "rust-crosssdk-${TUNE_PKGARCH}-${RUST_LIBC}"
