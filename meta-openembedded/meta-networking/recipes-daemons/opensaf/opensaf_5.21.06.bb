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

DEPENDS = "libxml2 python3"
TOOLCHAIN = "gcc"

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/releases/${BPN}-${PV}.tar.gz \
           file://0001-configure-Pass-linker-specific-options-with-Wl.patch \
           file://0001-configure-Disable-format-overflow-if-supported-by-gc.patch \
           file://0001-src-Add-missing-header-limits.h-for-_POSIX_HOST_NAME.patch \
           file://0001-immpbe_dump.cc-Use-sys-wait.h-instead-of-wait.h.patch \
           file://0001-create_empty_library-Use-CC-variable-intead-of-hardc.patch \
           file://0001-immom_python-convert-to-python3.patch \
           file://0001-Fix-build-with-fno-common.patch \
           file://0001-Use-correct-printf-format-for-__fsblkcnt_t.patch \
           "
SRC_URI[md5sum] = "a60775787ba520a0b1031fcd42e0d65b"
SRC_URI[sha256sum] = "d29d124506e4b084285d27c8742c7bca66de80be6a0ba9de8e37835ccaa8ee57"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/opensaf/files/releases"

inherit autotools useradd systemd pkgconfig

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-f -r opensaf"
USERADD_PARAM:${PN} =  "-r -g opensaf -d ${datadir}/opensaf/ -s ${sbindir}/nologin -c \"OpenSAF\" opensaf"

SYSTEMD_SERVICE:${PN} += "opensafd.service"
SYSTEMD_AUTO_ENABLE = "disable"

PACKAGECONFIG[systemd] = ",,systemd"
PACKAGECONFIG[openhpi] = "--with-hpi-interface=B03,,openhpi"
PACKAGECONFIG[plm] = "--enable-ais-plm,--disable-ais-plm,libvirt openhpi"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', ' systemd', '', d)}"

CPPFLAGS += "-Wno-error=stringop-overflow= -Wno-error=stringop-truncation"
CXXFLAGS += "-Wno-error=stringop-overflow= -Wno-error=stringop-truncation -Wno-error=format-truncation="
LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

do_install:append() {
    rm -fr "${D}${localstatedir}/lock"
    rm -fr "${D}${localstatedir}/run"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
    rmdir --ignore-fail-on-non-empty "${D}${datadir}/java"
    if [ ! -d "${D}${sysconfdir}/init.d" ]; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${B}/osaf/services/infrastructure/nid/scripts/opensafd ${D}${sysconfdir}/init.d/
    fi
}

FILES:${PN} += "${libdir}/libSa*.so ${systemd_unitdir}/system/*.service"
FILES:${PN}-dev += "${libdir}/libopensaf_core.so"
FILES:${PN}-staticdev += "${PKGLIBDIR}/*.a"

INSANE_SKIP:${PN} = "dev-so"

RDEPENDS:${PN} += "bash python3-core"

# http://errors.yoctoproject.org/Errors/Details/186970/
COMPATIBLE_HOST:libc-musl = 'null'

FILES_SOLIBSDEV = ""
