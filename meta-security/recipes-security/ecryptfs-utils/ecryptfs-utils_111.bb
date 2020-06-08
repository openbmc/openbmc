SUMMARY = "The eCryptfs mount helper and support libraries"
DESCRIPTION = "eCryptfs is a stacked cryptographic filesystem \
    that ships in Linux kernel versions 2.6.19 and above. This \
    package provides the mount helper and supporting libraries \
    to perform key management and mount functions."
HOMEPAGE = "https://launchpad.net/ecryptfs"
SECTION = "base"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

DEPENDS = "keyutils libgcrypt intltool-native glib-2.0-native"

SRC_URI = "\
    https://launchpad.net/ecryptfs/trunk/${PV}/+download/${BPN}_${PV}.orig.tar.gz \
    file://ecryptfs-utils-CVE-2016-6224.patch \
    file://0001-avoid-race-condition.patch \
    file://ecryptfs.service \
    "

SRC_URI[md5sum] = "83513228984f671930752c3518cac6fd"
SRC_URI[sha256sum] = "112cb3e37e81a1ecd8e39516725dec0ce55c5f3df6284e0f4cc0f118750a987f"

inherit autotools pkgconfig systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "ecryptfs.service"

EXTRA_OECONF = "\
    --libdir=${base_libdir} \
    --disable-pywrap \
    --disable-nls \
    --with-pamdir=${base_libdir}/security \
    --disable-openssl \
    "

PACKAGECONFIG ??= "nss \
    ${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)} \
    "
PACKAGECONFIG[nss] = "--enable-nss,--disable-nss,nss,"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam,"

do_configure_prepend() {
    export NSS_CFLAGS="-I${STAGING_INCDIR}/nspr -I${STAGING_INCDIR}/nss3"
    export NSS_LIBS="-L${STAGING_BASELIBDIR} -lssl3 -lsmime3 -lnss3 -lsoftokn3 -lnssutil3"
    export KEYUTILS_CFLAGS="-I${STAGING_INCDIR}"
    export KEYUTILS_LIBS="-L${STAGING_LIBDIR} -lkeyutils"
    sed -i -e "s;rootsbindir=\"/sbin\";rootsbindir=\"\${base_sbindir}\";g" ${S}/configure.ac
}

do_install_append() {
    chmod 4755 ${D}${base_sbindir}/mount.ecryptfs_private
    # ${base_libdir} is identical to ${libdir} when usrmerge enabled
    if ! ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','true','false',d)}; then
        mkdir -p ${D}/${libdir}
        mv ${D}/${base_libdir}/pkgconfig ${D}/${libdir}
    fi
    sed -i -e 's:-I${STAGING_INCDIR}::' \
           -e 's:-L${STAGING_LIBDIR}::' ${D}/${libdir}/pkgconfig/libecryptfs.pc
    sed -i -e "s: ${base_sbindir}/cryptsetup: ${sbindir}/cryptsetup:" ${D}${bindir}/ecryptfs-setup-swap
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -D -m 0644 ${WORKDIR}/ecryptfs.service ${D}${systemd_system_unitdir}/ecryptfs.service
    fi
}

FILES_${PN} += "${base_libdir}/security/* ${base_libdir}/ecryptfs/*"

RDEPENDS_${PN} += "cryptsetup"
RRECOMMENDS_${PN} = "gettext-runtime"
