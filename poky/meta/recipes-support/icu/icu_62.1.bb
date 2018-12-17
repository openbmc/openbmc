require icu.inc

LIC_FILES_CHKSUM = "file://../LICENSE;md5=63752c57bd0b365c9af9f427ef79c819"

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
           file://0002-Add-ARC-support.patch \
           "

SRC_URI_append_class-target = "\
           file://0001-Disable-LDFLAGSICUDT-for-Linux.patch \
          "
SRC_URI[md5sum] = "490ad9d920158e0314e10ba74ae9a150"
SRC_URI[sha256sum] = "3dd9868d666350dda66a6e305eecde9d479fb70b30d5b55d78a1deffb97d5aa3"

UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)/"
UPSTREAM_CHECK_URI = "http://download.icu-project.org/files/icu4c/"
