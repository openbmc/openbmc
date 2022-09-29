require gem5-source_20.inc

SRC_URI += "file://0001-dev-arm-SMMUv3-enable-interrupt-interface.patch"

BPN = "gem5-aarch64-native"

require gem5-aarch64-native.inc
require gem5-native.inc

# Get rid of compiler errors when building protobuf
GEM5_SCONS_ARGS:append = " CCFLAGS_EXTRA='-Wno-error=unused-variable' --verbose"

# Get rid of linker errors and have a faster link process
GEM5_SCONS_ARGS:append = " LDFLAGS_EXTRA='${BUILD_LDFLAGS}' \
MARSHAL_LDFLAGS_EXTRA='${BUILD_LDFLAGS}' --force-lto "

do_compile:prepend() {
    # Gem5 expect to have python in the path (can be python2 or 3)
    # Create a link named python to python3
    real=$(which ${PYTHON})
    ln -snf $real $(dirname $real)/python
}
