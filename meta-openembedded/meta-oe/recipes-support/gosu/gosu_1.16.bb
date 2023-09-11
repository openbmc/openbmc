SUMMARY = "Simple Go-based setuid+setgid+setgroups+exec"
HOMEPAGE = "https://github.com/tianon/gosu"
DESCRIPTION = "This is a simple tool grown out of the simple fact that su and sudo have very strange and often annoying TTY and signal-forwarding behavior."
LICENSE = "Apache-2.0 "
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

GO_IMPORT = "github.com/tianon/gosu"
SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https \
           git://github.com/opencontainers/runc;name=runc;destsuffix=${S}/src/github.com/opencontainers/runc;branch=main;protocol=https \
"
SRCREV = "0e7347714352cd7f2e5edc9d2cf838d9934e6036"
#v1.1.0
SRCREV_runc = "067aaf8548d78269dcb2c13b856775e27c410f9c"

SRCREV_FORMAT .= "_runc"

inherit go

CGO_ENABLED = "1"

do_compile:prepend() {
    # New Go versions has Go modules support enabled by default
    export GO111MODULE=off
}

RDEPENDS:${PN}-dev += "bash"
