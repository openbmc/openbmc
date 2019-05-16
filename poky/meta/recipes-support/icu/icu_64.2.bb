require icu.inc

LIC_FILES_CHKSUM = "file://../LICENSE;md5=8bc5d32052a96f214cbdd1e53dfc935d"

def icu_download_version(d):
    pvsplit = d.getVar('PV').split('.')
    return pvsplit[0] + "_" + pvsplit[1]

ICU_PV = "${@icu_download_version(d)}"

# http://errors.yoctoproject.org/Errors/Details/20486/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

BASE_SRC_URI = "http://download.icu-project.org/files/icu4c/${PV}/icu4c-${ICU_PV}-src.tgz"
SRC_URI = "${BASE_SRC_URI} \
           file://icu-pkgdata-large-cmd.patch \
           file://fix-install-manx.patch \
           file://0001-Fix-big-endian-build.patch \
           file://0001-icu-Added-armeb-support.patch \
           "

SRC_URI_append_class-target = "\
           file://0001-Disable-LDFLAGSICUDT-for-Linux.patch \
          "
SRC_URI[md5sum] = "a3d18213beec454e3cdec9a3116d6b05"
SRC_URI[sha256sum] = "627d5d8478e6d96fc8c90fed4851239079a561a6a8b9e48b0892f24e82d31d6c"

UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"
UPSTREAM_CHECK_URI = "http://download.icu-project.org/files/icu4c/"
