SUMMARY = "Simple Go-based setuid+setgid+setgroups+exec"
HOMEPAGE = "https://github.com/tianon/gosu"
DESCRIPTION = "This is a simple tool grown out of the simple fact that su and sudo have very strange and often annoying TTY and signal-forwarding behavior."
LICENSE = "Apache-2.0 "
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

GO_IMPORT = "github.com/tianon/gosu"
SRC_URI = "git://${GO_IMPORT}.git;branch=master;protocol=https \
           git://github.com/opencontainers/runc;name=runc;destsuffix=${S}/src/github.com/opencontainers/runc;branch=main;protocol=https \
"
SRCREV = "9f7cd138a1ebc0684d43ef6046bf723978e8741f"
SRCREV_runc = "d7f7b22a85a2387557bdcda125710c2506f8d5c5"
inherit go

do_compile:prepend() {
    # New Go versions has Go modules support enabled by default
    export GO111MODULE=off
}

RDEPENDS:${PN}-dev += "bash"
