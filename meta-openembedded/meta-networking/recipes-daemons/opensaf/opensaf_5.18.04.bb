SUMMARY = "OpenSAF is an open source implementation of the SAF AIS specification"
DESCRIPTION = "OpenSAF is an open source project established to develop a base platform \
middleware consistent with Service Availability Forum (SA Forum) \
specifications, under the LGPLv2.1 license. The OpenSAF Foundation was \
established by leading Communications and Enterprise Computing Companies to \
facilitate the OpenSAF Project and to accelerate the adoption of the OpenSAF \
code base in commercial products. \
The OpenSAF project was launched in mid 2007 and has been under development by \
an informal group of supporters of the OpenSAF initiative. The OpenSAF \
Foundation was founded on January 22nd 2008 with Emerson Network Power, \
Ericsson, Nokia Siemens Networks, HP and Sun Microsystems as founding members."
HOMEPAGE = "http://www.opensaf.org"
SECTION = "admin"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7"

DEPENDS = "libxml2 python"
TOOLCHAIN = "gcc"

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/releases/${BPN}-${PV}.tar.gz \
           file://0001-configure-Pass-linker-specific-options-with-Wl.patch \
           file://0001-configure-Disable-format-overflow-if-supported-by-gc.patch \
           file://0001-src-Add-missing-header-limits.h-for-_POSIX_HOST_NAME.patch \
           file://0001-immpbe_dump.cc-Use-sys-wait.h-instead-of-wait.h.patch \
           file://0001-Catch-std-ifstream-failure-by-reference.patch \
           file://0002-Fix-format-truncation-errors.patch \
           file://0001-Fix-string-overflow-in-snprintf.patch \
           file://0008-check-for-size-before-using-strncpy.patch \
           "
SRC_URI[md5sum] = "21836e43b13ad33bed9bd0ed391e5a6e"
SRC_URI[sha256sum] = "e55dc2645487fb22938e8386b99eef6eb7aff43a246ce3e92488daf6ee46247a"

inherit autotools useradd systemd pkgconfig

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-f -r opensaf"
USERADD_PARAM_${PN} =  "-r -g opensaf -d ${datadir}/opensaf/ -s ${sbindir}/nologin -c \"OpenSAF\" opensaf"

SYSTEMD_SERVICE_${PN} += "opensafd.service"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGECONFIG[systemd] = ",,systemd"
PACKAGECONFIG[openhpi] = "--with-hpi-interface=B03,,openhpi"
PACKAGECONFIG[plm] = "--enable-ais-plm,--disable-ais-plm,libvirt openhpi"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)}"

PKGLIBDIR="${libdir}"

LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

do_install_append() {
    cp -av --no-preserve=ownership ${B}/lib/.libs/*.so* ${D}${libdir}
    rm -fr "${D}${localstatedir}/lock"
    rm -fr "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
    rmdir --ignore-fail-on-non-empty "${D}${datadir}/java"
    if [ ! -d "${D}${sysconfdir}/init.d" ]; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${B}/osaf/services/infrastructure/nid/scripts/opensafd ${D}${sysconfdir}/init.d/
    fi
}

FILES_${PN} += "${systemd_unitdir}/system/*.service"
FILES_${PN}-staticdev += "${PKGLIBDIR}/*.a"

INSANE_SKIP_${PN} = "dev-so"

RDEPENDS_${PN} += "bash python"

# http://errors.yoctoproject.org/Errors/Details/186970/
EXCLUDE_FROM_WORLD_libc-musl = "1"
