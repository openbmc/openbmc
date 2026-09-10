SUMMARY = "Boost.Build"
DESCRIPTION = "B2 makes it easy to build C++ projects, everywhere."
HOMEPAGE = "https://github.com/boostorg/build"
SECTION = "devel"

LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/boostorg/build;protocol=https;branch=master;tag=boost-${PV} \
           file://add-python-numpy-include-feature.patch \
           file://python-glibc.patch"
SRCREV = "05b6b638b54e1f013fb8b281f5020e337be31363"
PE = "1"

UPSTREAM_CHECK_GITTAGREGEX = "boost-(?P<pver>(\d+(\.\d+)+))"

inherit native

do_compile() {
    ./bootstrap.sh
}

do_install() {
    HOME=/var/run ./b2 install --prefix=${D}${prefix}
}

# The build is either release mode (pre-stripped) or debug (-O0).
INSANE_SKIP:${PN} = "already-stripped"
