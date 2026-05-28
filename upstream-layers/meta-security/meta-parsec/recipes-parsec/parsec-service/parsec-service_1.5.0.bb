SUMMARY = "Platform AbstRaction for SECurity Daemon"
HOMEPAGE = "https://github.com/parallaxsecond/parsec"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit cargo pkgconfig cargo-update-recipe-crates

DEPENDS += "clang-native"

SRC_URI += "crate://crates.io/parsec-service/${PV} \
            file://parsec_init \
            file://systemd.patch \
            file://parsec-tmpfiles.conf \
"
SRC_URI[parsec-service-1.5.0.sha256sum] = "819f1f658a1c62fee1b4986fe33232d5dc539b37ae0933981e02737ad7fb570f"

B = "${CARGO_VENDORING_DIRECTORY}/${BP}"

have_TPM = "${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'TPM', '', d)}"
PACKAGECONFIG ??= "PKCS11 MBED-CRYPTO ${@bb.utils.contains('BBFILE_COLLECTIONS', 'tpm-layer', '${have_TPM}', '', d)}"

PACKAGECONFIG[TPM] = "-F tpm-provider -F tss-esapi/generate-bindings,,tpm2-tss,tpm2-tss libtss2-tcti-device"
PACKAGECONFIG[PKCS11] = "-F pkcs11-provider -F cryptoki/generate-bindings,"
PACKAGECONFIG[MBED-CRYPTO] = "-F mbed-crypto-provider,"
PACKAGECONFIG[CRYPTOAUTHLIB] = "-F cryptoauthlib-provider,"
# Current version of Parsec is not compatible with the current version of Trusted Services
#PACKAGECONFIG[TS] = "-F trusted-service-provider,,libts protobuf-native,libts"

export BINDGEN_EXTRA_CLANG_ARGS
target = "${@d.getVar('TARGET_SYS').replace('-', ' ')}"
BINDGEN_EXTRA_CLANG_ARGS = "${@bb.utils.contains('target', 'arm', \
                              '--sysroot=${WORKDIR}/recipe-sysroot -I${WORKDIR}/recipe-sysroot/usr/include -mfloat-abi=hard', \
                              '--sysroot=${WORKDIR}/recipe-sysroot -I${WORKDIR}/recipe-sysroot/usr/include', \
                              d)}"

inherit systemd
SYSTEMD_SERVICE:${PN} = "parsec.service"

inherit update-rc.d
INITSCRIPT_NAME = "parsec"

# A local file can be defined in build/local.conf
# The file should also be included into SRC_URI then
PARSEC_CONFIG ?= "${S}/config.toml"


do_install () {
    # Binaries
    install -d -m 700 -o parsec -g parsec "${D}${libexecdir}/parsec"
    install -m 700 -o parsec -g parsec "${B}/target/${CARGO_TARGET_SUBDIR}/parsec" ${D}${libexecdir}/parsec/parsec

    # Config file
    install -d -m 700 -o parsec -g parsec "${D}${sysconfdir}/parsec"
    install -m 400 -o parsec -g parsec "${PARSEC_CONFIG}" ${D}${sysconfdir}/parsec/config.toml

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${S}/systemd-daemon/parsec.service ${D}${systemd_unitdir}/system

        install -d ${D}${libdir}/tmpfiles.d
        install -m 644 ${UNPACKDIR}/parsec-tmpfiles.conf ${D}${libdir}/tmpfiles.d
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 755 ${UNPACKDIR}/parsec_init ${D}${sysconfdir}/init.d/parsec
        # Data dir
        install -d -m 700 -o parsec -g parsec "${D}${localstatedir}/lib/parsec"
    fi
}

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-r parsec"
USERADD_PARAM:${PN} = "\
    --system \
    --gid parsec \
    --shell /usr/sbin/nologin \
    --home-dir ${localstatedir}/lib/parsec \
    ${@parsec_groups(d)} \
    parsec"

def parsec_groups(d):
    groups = []
    config = d.getVar("PACKAGECONFIG").split()
    if "TPM" in config:
        groups.append("tss")
    if "TS" in config:
        groups.append("tee")
    return "--groups " + ",".join(groups) if groups else ""

FILES:${PN} += " \
    ${sysconfdir}/parsec/config.toml \
    ${libexecdir}/parsec/parsec \
    ${systemd_unitdir}/system/parsec.service \
    ${libdir}/tmpfiles.d/parsec-tmpfiles.conf \
    ${sysconfdir}/init.d/parsec \
"

require parsec-service-crates.inc

# The QA check has been temporarily disabled. An issue has been created
# upstream to fix this.
# https://github.com/parallaxsecond/parsec/issues/645
INSANE_SKIP:${PN}-dbg += "buildpaths"
