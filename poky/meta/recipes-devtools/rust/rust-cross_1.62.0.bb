require rust.inc
inherit cross
require rust-cross.inc
require rust-source.inc

DEPENDS += "virtual/${TARGET_PREFIX}gcc virtual/${TARGET_PREFIX}compilerlibs virtual/libc"
PROVIDES = "virtual/${TARGET_PREFIX}rust"
PN = "rust-cross-${TUNE_PKGARCH}-${TCLIBC}"
