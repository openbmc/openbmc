SUMMARY = "Tools for manipulating FFS flash images."
DESCRIPTION = "FFS is the FSP Flash File Structure which is also currently \
used on OpenPOWER machines for the flash layout."

HOMEPAGE = "https://github.com/open-power/ffs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=34400b68072d710fecd0a2940a0d1658"

inherit autotools pkgconfig

ALLOW_EMPTY_${PN} = "1"
PACKAGE_BEFORE_PN += "${PN}-ecc ${PN}-deprecated"

FILES_${PN}-ecc = "${bindir}/ecc"
FILES_${PN}-deprecated = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
