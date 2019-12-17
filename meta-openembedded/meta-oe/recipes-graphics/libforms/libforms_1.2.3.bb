DESCRIPTION = "The XForms graphical interface widget library	"
HOMEPAGE = "http://savannah.nongnu.org/projects/xforms/"
PR = "r0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=ade9a053df81f5b9408d2f4f5546df86"

SRC_URI = "http://download.savannah.nongnu.org/releases/xforms/xforms-${PV}.tar.gz \
           file://fix-link-to-xforms-man.patch \
           file://add-absolute-path-for-include-dir.patch \
           file://fix-path-fdesign_LDADD.patch \
          "

SRC_URI[md5sum] = "235720a758a8b8d9e6e452dc67190e9b"
SRC_URI[sha256sum] = "7989b39598c769820ad451ad91e5cb0de29946940c8240aac94ca8238c2def61"

inherit autotools features_check

REQUIRED_DISTRO_FEATURES = "opengl x11"

S = "${WORKDIR}/xforms-${PV}"

DEPENDS = "libxpm jpeg libx11 mesa"
RDEPENDS_${PN} = "bash"

EXTRA_OECONF = "--with-extra-inc=${S}/lib" 
