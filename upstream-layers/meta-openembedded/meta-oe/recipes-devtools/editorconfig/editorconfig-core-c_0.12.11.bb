SUMMARY = "EditorConfig helps maintain consistent coding styles across various editors and IDEs."
HOMEPAGE = "https://editorconfig.org"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=38f617473e9f7373b5e79baf437accf8"

SRC_URI = "git://github.com/editorconfig/editorconfig-core-c.git;protocol=https;branch=master;tag=v${PV}"

SRCREV = "0ba157eff167c1b2d1bc0c3e13975b7a73fb9d25"

inherit cmake

DEPENDS = "pcre2"

do_install:append() {
    sed -i -e 's|${STAGING_DIR_HOST}||g' ${D}${libdir}/cmake/EditorConfig/EditorConfigTargets.cmake
}
