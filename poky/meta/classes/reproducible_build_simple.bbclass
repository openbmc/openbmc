# Setup default environment for reproducible builds.

BUILD_REPRODUCIBLE_BINARIES = "1"

export PYTHONHASHSEED = "0"
export PERL_HASH_SEED = "0"
export SOURCE_DATE_EPOCH ??= "1520598896"

REPRODUCIBLE_TIMESTAMP_ROOTFS ??= "1520598896"

