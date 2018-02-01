require go-cross.inc
require go_${PV}.bb

# Go binaries are not understood by the strip tool.
INHIBIT_SYSROOT_STRIP = "1"
