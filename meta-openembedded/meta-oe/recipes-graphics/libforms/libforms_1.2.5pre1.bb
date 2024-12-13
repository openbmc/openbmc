DESCRIPTION = "The XForms graphical interface widget library	"
HOMEPAGE = "http://savannah.nongnu.org/projects/xforms/"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=ade9a053df81f5b9408d2f4f5546df86"

SRC_URI = "http://download.savannah.nongnu.org/releases/xforms/xforms-${PV}.tar.gz \
           file://fix-link-to-xforms-man.patch \
           file://add-absolute-path-for-include-dir.patch \
           file://fix-path-fdesign_LDADD.patch \
           file://0001-Make-extern-declarations-in-header-file.patch \
           file://0001-Modify-include-dir.patch \
          "

SRC_URI[sha256sum] = "92b5e6466ea2dffca7332aec12734e65b3e961825eb3100b7d889c0d1abb4697"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

S = "${WORKDIR}/xforms-${PV}"

DEPENDS = "libxpm jpeg libx11 virtual/libgl"
RDEPENDS:${PN} = "bash"

EXTRA_OECONF = "--with-extra-inc=${S}/lib" 

do_compile:append() {
    sed -i -e 's|${B}|.|' ${B}/fd2ps/fd2ps
    sed -i -e 's|${B}|.|' ${B}/fdesign/fdesign
}
