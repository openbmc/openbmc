# include this in your distribution to easily switch between versions
# just by changing RUST_VERSION variable

RUST_VERSION ?= "1.51.0"

PREFERRED_VERSION_cargo ?= "${RUST_VERSION}"
PREFERRED_VERSION_cargo-native ?= "${RUST_VERSION}"
PREFERRED_VERSION_libstd-rs ?= "${RUST_VERSION}"
PREFERRED_VERSION_rust ?= "${RUST_VERSION}"
PREFERRED_VERSION_rust-cross-${TARGET_ARCH} ?= "${RUST_VERSION}"
PREFERRED_VERSION_rust-llvm ?= "${RUST_VERSION}"
PREFERRED_VERSION_rust-llvm-native ?= "${RUST_VERSION}"
PREFERRED_VERSION_rust-native ?= "${RUST_VERSION}"
