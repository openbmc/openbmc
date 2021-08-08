SUMMARY = "Platform AbstRaction for SECurity Daemon"
HOMEPAGE = "https://github.com/parallaxsecond/parsec"
LICENSE = "Apache-2.0"

inherit cargo

SRC_URI += "crate://crates.io/parsec-service/${PV} \
            file://parsec_init \
            file://systemd.patch \
            file://parsec-tmpfiles.conf \
"

DEPENDS = "tpm2-tss clang-native"

CARGO_BUILD_FLAGS += " --features all-providers,cryptoki/generate-bindings,tss-esapi/generate-bindings"

inherit systemd
SYSTEMD_SERVICE:${PN} = "parsec.service"

inherit update-rc.d
INITSCRIPT_NAME = "parsec"

# A local file can be defined in build/local.conf
# The file should also be included into SRC_URI then
PARSEC_CONFIG ?= "${S}/config.toml"

do_install:append () {
    # Binaries
    install -d -m 700 -o parsec -g parsec "${D}${libexecdir}/parsec"
    install -m 700 -o parsec -g parsec "${WORKDIR}/build/target/${CARGO_TARGET_SUBDIR}/parsec" ${D}${libexecdir}/parsec/parsec

    # Config file
    install -d -m 700 -o parsec -g parsec "${D}${sysconfdir}/parsec"
    install -m 400 -o parsec -g parsec "${PARSEC_CONFIG}" ${D}${sysconfdir}/parsec/config.toml

    # Data dir
    install -d -m 700 -o parsec -g parsec "${D}${localstatedir}/lib/parsec"

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${S}/systemd-daemon/parsec.service ${D}${systemd_unitdir}/system

        install -d ${D}${libdir}/tmpfiles.d
        install -m 644 ${WORKDIR}/parsec-tmpfiles.conf ${D}${libdir}/tmpfiles.d
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 755 ${WORKDIR}/parsec_init ${D}${sysconfdir}/init.d/parsec
    fi
}

inherit useradd
USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "-r -g parsec -s /bin/false -d ${localstatedir}/lib/parsec parsec"
GROUPADD_PARAM:${PN} = "-r parsec"

FILES:${PN} += " \
    ${sysconfdir}/parsec/config.toml \
    ${libexecdir}/parsec/parsec \
    ${systemd_unitdir}/system/parsec.service \
    ${libdir}/tmpfiles.d/parsec-tmpfiles.conf \
    ${sysconfdir}/init.d/parsec \
"

require parsec-service_${PV}.inc
