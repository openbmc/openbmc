require rust.inc

DEPENDS += "rust-llvm (=${PV})"

# Otherwise we'll depend on what we provide
INHIBIT_DEFAULT_RUST_DEPS:class-native = "1"
# We don't need to depend on gcc-native because yocto assumes it exists
PROVIDES:class-native = "virtual/${TARGET_PREFIX}rust"

BBCLASSEXTEND = "native nativesdk"
