SUMMARY = "EditorConfig helps maintain consistent coding styles across various editors and IDEs."
HOMEPAGE = "https://https://editorconfig.org"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f515fff3ea0a2b9797eda60d83c0e5ca"

SRC_URI = "git://github.com/editorconfig/editorconfig-core-c.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "b7837029494c03af5ea70ed9d265e8c2123bff53"

inherit cmake

DEPENDS = "pcre2"

