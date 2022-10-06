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

SRC_URI[sha256sum] = "78cc6b07071bbeaa1f906e0a22d5e9980e48f8913577bc082d661afe5cb75696"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

S = "${WORKDIR}/xforms-${PV}"

DEPENDS = "libxpm jpeg libx11 mesa"
RDEPENDS:${PN} = "bash"

EXTRA_OECONF = "--with-extra-inc=${S}/lib" 
