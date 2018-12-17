DESCRIPTION = "The Openobex project is an open source implementation of the \
Object Exchange (OBEX) protocol."
HOMEPAGE = "http://openobex.triq.net"
SECTION = "libs"
LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

DEPENDS = "virtual/libusb0"
DEPENDS_append_class-target = " ${@bb.utils.contains('DISTRO_FEATURES','bluez5','bluez5','bluez4',d)}"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}/${PV}/${BP}-Source.tar.gz \
"
SRC_URI[md5sum] = "f6e0b6cb7dcfd731460a7e9a91429a3a"
SRC_URI[sha256sum] = "158860aaea52f0fce0c8e4b64550daaae06df2689e05834697b7e8c7d73dd4fc"

S = "${WORKDIR}/${BP}-Source"

inherit cmake pkgconfig

EXTRA_OECONF = " -DCMAKE_SKIP_RPATH=ON "
EXTRA_OECMAKE += "-DBUILD_DOCUMENTATION=OFF"

ASNEEDED = ""

do_install_append () {
    rmdir ${D}${bindir}
}

PACKAGES =+ "openobex-apps"
FILES_${PN}-apps = "${bindir}/*"
FILES_${PN} += "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${bindir}/openobex-config"
DEBIAN_NOAUTONAME_${PN}-apps = "1"

BBCLASSEXTEND = "native"
