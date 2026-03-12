SUMMARY = "Test recipe for fetching git submodules as second repo"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/git-submodule-test/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "git://git.yoctoproject.org/git-submodule-test;branch=master;name=repo-git \
    gitsm://git.yoctoproject.org/git-submodule-test;branch=master;name=repo-gitsm;destsuffix=${BB_GIT_DEFAULT_DESTSUFFIX}/nested/repo-gitsm"
SRCREV_repo-git = "a2885dd7d25380d23627e7544b7bbb55014b16ee"
SRCREV_repo-gitsm = "a2885dd7d25380d23627e7544b7bbb55014b16ee"
SRCREV_FORMAT = "repo-git_repo-gitsm"
