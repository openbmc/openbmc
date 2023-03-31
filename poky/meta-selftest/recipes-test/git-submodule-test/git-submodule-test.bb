SUMMARY = "Test recipe for fetching git submodules"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/git-submodule-test/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

UPSTREAM_VERSION_UNKNOWN = "1"

SRC_URI = "gitsm://git.yoctoproject.org/git-submodule-test;branch=master"
SRCREV = "a2885dd7d25380d23627e7544b7bbb55014b16ee"

S = "${WORKDIR}/git"

do_test_git_as_user() {
    cd ${S}
    git status
}
addtask test_git_as_user after do_unpack

fakeroot do_test_git_as_root() {
    cd ${S}
    git status
}
do_test_git_as_root[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask test_git_as_root after do_unpack
