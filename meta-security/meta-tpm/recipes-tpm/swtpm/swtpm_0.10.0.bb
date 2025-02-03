SUMMARY = "SWTPM - Software TPM Emulator"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fe8092c832b71ef20dfe4c6d3decb3a8"
SECTION = "apps"

# expect-native, socat-native, coreutils-native and net-tools-native are reportedly only required for the tests
DEPENDS = "libtasn1 coreutils-native expect-native socat-native glib-2.0 net-tools-native libtpms json-glib"

SRCREV = "54f4bb1e702a8b80d990ca00b6f72d5031dd131a"
SRC_URI = "git://github.com/stefanberger/swtpm.git;branch=stable-0.10;protocol=https"
PE = "2"

S = "${WORKDIR}/git"

PARALLEL_MAKE = ""
inherit autotools pkgconfig perlnative

TSS_USER="tss"
TSS_GROUP="tss"

PACKAGECONFIG ?= "openssl gnutls"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'seccomp', 'seccomp', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('BBFILE_COLLECTIONS', 'filesystems-layer', 'cuse', '', d)}"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
# expect, bash, tpm2-pkcs11-tools (tpm2_ptool), tpmtool and certtool is
# used by swtpm-create-tpmca (the last two is provided by gnutls)
# gnutls is required by: swtpm-create-tpmca, swtpm-localca and swtpm_cert
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls-native gnutls, gnutls-bin expect bash tpm2-pkcs11-tools"
PACKAGECONFIG[selinux] = "--with-selinux, --without-selinux, libselinux"
PACKAGECONFIG[cuse] = "--with-cuse, --without-cuse, fuse"
PACKAGECONFIG[seccomp] = "--with-seccomp, --without-seccomp, libseccomp"

EXTRA_OECONF += "--with-tss-user=${TSS_USER} --with-tss-group=${TSS_GROUP}"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${TSS_USER}"
USERADD_PARAM:${PN} = "--system -g ${TSS_GROUP} --home-dir / \
    --no-create-home  --shell /bin/false ${BPN}"


PACKAGE_BEFORE_PN = "${PN}-cuse"
FILES:${PN}-cuse = "${bindir}/swtpm_cuse"

INSANE_SKIP:${PN}   += "dev-so"

RDEPENDS:${PN} = "libtpms"

BBCLASSEXTEND = "native nativesdk"
