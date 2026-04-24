SUMMARY = "Test recipe for fetching git submodules as second repo"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/git-submodule-test/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git.yoctoproject.org/git-submodule-test;branch=master;name=repo-git;protocol=https \
    gitsm://git.yoctoproject.org/git-submodule-test;branch=master;name=repo-gitsm;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/nested/repo-gitsm;protocol=https"
SRCREV_repo-git = "f280847494763cdcf71197557a81ba7d8a6bce42"
SRCREV_repo-gitsm = "f280847494763cdcf71197557a81ba7d8a6bce42"
SRCREV_FORMAT = "repo-git_repo-gitsm"
