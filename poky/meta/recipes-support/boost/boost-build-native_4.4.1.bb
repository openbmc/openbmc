SUMMARY = "Boost.Build"
DESCRIPTION = "B2 makes it easy to build C++ projects, everywhere."
HOMEPAGE = "https://github.com/boostorg/build"
SECTION = "devel"

LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/boostorg/build;protocol=https;branch=master"
SRCREV = "76da80f33187a3d9e5336157cdfae12ce82e37eb"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+){2,}))"

inherit native

S = "${WORKDIR}/git"

do_compile() {
    ./bootstrap.sh
}

do_install() {
    HOME=/var/run ./b2 install --prefix=${prefix} staging-prefix=${D}${prefix}
}

# The build is either release mode (pre-stripped) or debug (-O0).
INSANE_SKIP:${PN} = "already-stripped"
