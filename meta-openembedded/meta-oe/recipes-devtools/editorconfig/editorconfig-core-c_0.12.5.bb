SUMMARY = "EditorConfig helps maintain consistent coding styles across various editors and IDEs."
HOMEPAGE = "https://https://editorconfig.org"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f515fff3ea0a2b9797eda60d83c0e5ca"

SRC_URI = "git://github.com/editorconfig/editorconfig-core-c.git;protocol=https;branch=master"

S = "${WORKDIR}/git"
SRCREV = "f6b0ca395149b5a2fbd56a488cae30306a58170f"

inherit cmake

DEPENDS = "pcre2"

