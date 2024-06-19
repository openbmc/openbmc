SUMMARY = "EditorConfig helps maintain consistent coding styles across various editors and IDEs."
HOMEPAGE = "https://https://editorconfig.org"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38f617473e9f7373b5e79baf437accf8"

SRC_URI = "git://github.com/editorconfig/editorconfig-core-c.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "fd8cf1e94ecf4e6e4493833f96140cf9bd17578e"

inherit cmake

DEPENDS = "pcre2"

do_install:append() {
    sed -i -e 's|${STAGING_DIR_HOST}||g' ${D}${libdir}/cmake/EditorConfig/EditorConfigTargets.cmake
}
