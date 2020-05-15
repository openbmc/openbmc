SUMMARY = "SWTPM - Software TPM Emulator"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe8092c832b71ef20dfe4c6d3decb3a8"
SECTION = "apps"

DEPENDS = "libtasn1 expect socat glib-2.0 net-tools-native libtpm libtpm-native"

# configure checks for the tools already during compilation and
# then swtpm_setup needs them at runtime
DEPENDS += "tpm-tools-native expect-native socat-native"

SRCREV = "39673a0139b0ee14a0109aba50a0635592c672c4"
SRC_URI = "git://github.com/stefanberger/swtpm.git;branch=stable-${PV} \
           file://fix_fcntl_h.patch \
           file://ioctl_h.patch \
           "
PE = "1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
PARALLEL_MAKE = ""

TSS_USER="tss"
TSS_GROUP="tss"

PACKAGECONFIG ?= "openssl"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', 'cuse', '', d)}"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux"
PACKAGECONFIG[cuse] = "--with-cuse, --without-cuse, fuse"
PACKAGECONFIG[seccomp] = "--with-seccomp, --without-seccomp, libseccomp"

EXTRA_OECONF += "--with-tss-user=${TSS_USER} --with-tss-group=${TSS_GROUP}"

export SEARCH_DIR = "${STAGING_LIBDIR_NATIVE}"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system ${TSS_USER}"
USERADD_PARAM_${PN} = "--system -g ${TSS_GROUP} --home-dir  \
    --no-create-home  --shell /bin/false ${BPN}"

PACKAGE_BEFORE_PN = "${PN}-cuse"
FILES_${PN}-cuse = "${bindir}/swtpm_cuse"

INSANE_SKIP_${PN}   += "dev-so"

RDEPENDS_${PN} = "libtpm expect socat bash tpm-tools"

BBCLASSEXTEND = "native nativesdk"
