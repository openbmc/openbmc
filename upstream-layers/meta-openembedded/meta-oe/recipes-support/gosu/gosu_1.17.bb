SUMMARY = "Simple Go-based setuid+setgid+setgroups+exec"
HOMEPAGE = "https://github.com/tianon/gosu"
DESCRIPTION = "This is a simple tool grown out of the simple fact that su and sudo have very strange and often annoying TTY and signal-forwarding behavior."
LICENSE = "Apache-2.0 "
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

GO_IMPORT = "github.com/tianon/gosu"
SRC_URI = "git://${GO_IMPORT}.git;destsuffix=src/${GO_IMPORT};branch=master;protocol=https \
           git://github.com/moby/sys;name=user;destsuffix=src/github.com/moby/sys;branch=main;protocol=https \
           git://github.com/golang/sys;name=sys;destsuffix=src/golang.org/x/sys;branch=master;protocol=https \
"
SRCREV = "0d1847490b448a17eb347e5e357f2c0478df87ad"
#v0.1.0
SRCREV_user = "c0711cde08c8fa33857a2c28721659267f49b5e2"
#v0.1.0
SRCREV_sys = "95e765b1cc43ac521bd4fd501e00774e34401449"

SRCREV_FORMAT .= "_user_sys"

S = "${UNPACKDIR}"
inherit go

CGO_ENABLED = "1"

do_compile:prepend() {
    # New Go versions has Go modules support enabled by default
    export GO111MODULE=off
}

RDEPENDS:${PN}-dev += "bash"
