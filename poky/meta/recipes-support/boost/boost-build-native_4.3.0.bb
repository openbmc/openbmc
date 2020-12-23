SUMMARY = "Boost.Build"
SECTION = "devel"

LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/boostorg/build;protocol=https"
SRCREV = "632ea768f3eb225b4472c5ed6d20afee708724ad"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+){2,}))"

inherit native

S = "${WORKDIR}/git"

do_compile() {
    ./bootstrap.sh
}

do_install() {
    ./b2 install --prefix=${prefix} staging-prefix=${D}${prefix}
}

# The build is either release mode (pre-stripped) or debug (-O0).
INSANE_SKIP_${PN} = "already-stripped"
