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
LICENSE = "LGPL-2.1-only"
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
           file://0001-include-missing-array-header.patch \
           file://0002-configure-Disable-selected-warnings.patch \
           file://0001-include-cstdint-for-uintXX_t-types.patch \
           file://0002-Fix-Werror-enum-int-mismatch-with-gcc13.patch \
           "
SRC_URI[sha256sum] = "f008d53c83087ce2014c6089bc4ef08e14c1b4091298b943f4ceade1aa6bf61e"

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

CPPFLAGS += "-Wno-error"
CXXFLAGS += "-Wno-error"
LDFLAGS += "-Wl,--as-needed -latomic -Wl,--no-as-needed"

do_install:append() {
    rm -fr "${D}${localstatedir}/lock"
    rm -fr "${D}${localstatedir}/run"
    rmdir "${D}${localstatedir}/log/${BPN}/saflog"
    rmdir "${D}${localstatedir}/log/${BPN}"
    rmdir "${D}${localstatedir}/log"
    rmdir --ignore-fail-on-non-empty "${D}${localstatedir}"
    rmdir --ignore-fail-on-non-empty "${D}${datadir}/java"

    # Rename /etc/init.d/opensafd to /usr/lib/opensaf/opensafd-init as it is
    # needed by opensafd.service, but /etc/init.d is removed by systemd.bbclass
    # if sysvinit is not in DISTRO_FEATURES.
    mv ${D}${sysconfdir}/init.d/opensafd ${D}${libdir}/${BPN}/opensafd-init
    ln -srf ${D}${libdir}/${BPN}/opensafd-init ${D}${sysconfdir}/init.d/opensafd
    [ ! -f ${D}${systemd_system_unitdir}/opensafd.service ] ||
        sed -ri -e "s|/etc/init.d/opensafd|${libdir}/${BPN}/opensafd-init|" ${D}${systemd_system_unitdir}/opensafd.service

    # Create /var/log/opensaf/saflog in runtime.
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}" ]; then
        install -d ${D}${nonarch_libdir}/tmpfiles.d
        echo "d ${localstatedir}/log/${BPN}/saflog - - - -" > ${D}${nonarch_libdir}/tmpfiles.d/${BPN}.conf
    fi
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'sysvinit', d)}" ]; then
        install -d ${D}${sysconfdir}/default/volatiles
        echo "d root root 0755 ${localstatedir}/log/${BPN}/saflog none" > ${D}${sysconfdir}/default/volatiles/99_${BPN}
    fi
}

FILES:${PN} += "${libdir}/libSa*.so ${systemd_unitdir}/system/*.service"
FILES:${PN} += "${nonarch_libdir}/tmpfiles.d"
FILES:${PN}-dev += "${libdir}/libopensaf_core.so"
FILES:${PN}-staticdev += "${PKGLIBDIR}/*.a"

INSANE_SKIP:${PN} = "dev-so"

RDEPENDS:${PN} += "bash python3-core"

# http://errors.yoctoproject.org/Errors/Details/186970/
COMPATIBLE_HOST:libc-musl = 'null'

FILES_SOLIBSDEV = ""
