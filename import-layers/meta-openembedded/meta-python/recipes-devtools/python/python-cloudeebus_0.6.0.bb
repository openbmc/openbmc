DESCRIPTION = "A component which enables calling DBus methods and registering on DBus signals from Javascript"
HOMEPAGE = "https://github.com/01org/cloudeebus"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI = "git://github.com/01org/cloudeebus.git;protocol=http;branch=master "
SRCREV = "b5cc957eeabfe98cb996baf8e5a0ac848993c3d4"

S = "${WORKDIR}/git"

inherit distutils 

DEPENDS_${PN} = "python python-distribute"
RDEPENDS_${PN} = "python python-dbus python-json python-argparse python-pygobject python-autobahn python-twisted python-subprocess"

do_install_prepend() {
  install -d ${D}${PYTHON_SITEPACKAGES_DIR}/${PN}
}

DISTUTILS_INSTALL_ARGS = "--root=${D} \
    --single-version-externally-managed \
    --prefix=${prefix} \
    --install-lib=${PYTHON_SITEPACKAGES_DIR} \
    --install-data=${datadir}"

do_install_append() {
  distutils_do_install
  install -d ${D}${datadir}/doc/${BPN}/
  install -m 0644 ${S}/README.md ${D}${datadir}/doc/${BPN}/
}

FILES_${PN} += "${datadir}/cloudeebus"
FILES_${PN} += "${sysconfdir}/dbus-1/system.d/org.cloudeebus.conf"

