SUMMARY = "Test recipe for fetching git submodules"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/git-submodule-test/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

# Note: this is intentionally not the latest version in the original .bb
SRCREV = "132fea6e4dee56b61bcf5721c94e8b2445c6a017"
PV = "0.1+git"
PR = "r2"

SRC_URI = "gitsm://git.yoctoproject.org/git-submodule-test;branch=master"
UPSTREAM_CHECK_COMMITS = "1"
RECIPE_NO_UPDATE_REASON = "This recipe is used to test devtool upgrade feature"

EXCLUDE_FROM_WORLD = "1"

do_test_git_as_user() {
    cd ${S}
    git status
    git submodule status
}
addtask test_git_as_user after do_unpack

fakeroot do_test_git_as_root() {
    cd ${S}
    git status
    git submodule status
}
do_test_git_as_root[depends] += "virtual/fakeroot-native:do_populate_sysroot"
addtask test_git_as_root after do_unpack