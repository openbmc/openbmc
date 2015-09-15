require icu.inc

LIC_FILES_CHKSUM = "file://../license.html;md5=64eff4aadff4d104d6d437c4fde0e6d7"

def icu_download_version(d):
    pvsplit = d.getVar('PV', True).split('.')
    return pvsplit[0] + "_" + pvsplit[1]

ICU_PV = "${@icu_download_version(d)}"

BASE_SRC_URI = "http://download.icu-project.org/files/icu4c/${PV}/icu4c-${ICU_PV}-src.tgz"
SRC_URI = "${BASE_SRC_URI} \
           file://icu-pkgdata-large-cmd.patch \
          "

SRC_URI_append_class-target = "\
           file://0001-Disable-LDFLAGSICUDT-for-Linux.patch \
          "
SRC_URI[md5sum] = "e2d523df79d6cb7855c2fbe284f4db29"
SRC_URI[sha256sum] = "e16b22cbefdd354bec114541f7849a12f8fc2015320ca5282ee4fd787571457b"

