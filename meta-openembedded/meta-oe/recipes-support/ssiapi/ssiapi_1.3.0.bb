SUMMARY = "Intel RSTe with Linux OS SSI API Library"

DESCRIPTION = "Intel Rapid Storage Technology enterprise with Linux OS* Standard Storage Interface API Library. \
The library allows user to manage storage devices including creating and managing Raid arrays on systems with Intel chipset."

HOMEPAGE = "http://irstessi.sourceforge.net/"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9d701a2fbb56039fd64afb2262008ddb"

DEPENDS += "sg3-utils"

SRC_URI = "http://sourceforge.net/projects/irstessi/files/${BPN}.${PV}.tgz \
           file://0001-log-Avoid-shadowing-functions-from-std-lib.patch \
           file://0002-boost-Backport-clang-support.patch \
           file://0003-engine-Define-discover-const-String-path-in-base-cla.patch \
           file://0004-Do-not-override-flags-coming-from-build-environment.patch \
           file://0005-enable-out-of-source-tree-builds.patch \
           file://0001-Don-t-use-__GNUC_PREREQ.patch \
           file://0002-Use-stangard-int-types.patch \
           file://0003-replace-canonicalize_file_name-with-realpath.patch \
           file://0004-include-limits.h.patch \
           file://0001-Include-libgen.h.patch \
           "
SRC_URI[md5sum] = "d06c9b426437a7697d77266e9835b520"
SRC_URI[sha256sum] = "59daab29363d6e9f07c524029c4239653cfbbee6b0e57fd75df62499728dad8a"

S ="${WORKDIR}/${BPN}.${PV}"

inherit autotools

do_configure_prepend(){
    ${S}/autogen.sh
}

RDEPENDS_${PN} += "mdadm"

COMPATIBLE_HOST_powerpc = 'null'
