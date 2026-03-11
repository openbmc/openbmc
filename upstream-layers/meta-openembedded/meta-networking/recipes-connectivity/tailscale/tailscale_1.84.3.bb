SUMMARY = "Tailscale client and daemon"
DESCRIPTION = "The easiest, most secure way to use WireGuard and 2FA."
HOMEPAGE = "https://github.com/tailscale/tailscale"
SECTION = "networking"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=a672713a9eb730050e491c92edf7984d"
require ${BPN}-licenses.inc

MAJOR_MINOR = "${@oe.utils.trim_version('${PV}', 2)}"
SRC_URI = "git://github.com/tailscale/tailscale.git;protocol=https;branch=release-branch/${MAJOR_MINOR};destsuffix=${GO_SRCURI_DESTSUFFIX} \
           file://default \
           file://tailscaled.init \
           "
SRCREV = "7648989bc54738b1e40dde74fa822984a63cbc05"
SRCREV_SHORT = "${@d.getVar('SRCREV')[:8]}"
require ${BPN}-go-mods.inc

GO_IMPORT = "tailscale.com"
GO_INSTALL = "${GO_IMPORT}/cmd/tailscaled"
GO_LINKSHARED = ""
GOBUILDFLAGS:prepend = "-tags=${@','.join(d.getVar('PACKAGECONFIG_CONFARGS').split())} "
GO_EXTRA_LDFLAGS = "-X tailscale.com/version.longStamp=${PV}-${SRCREV_SHORT} -X tailscale.com/version.shortStamp=${PV}"

inherit go-mod update-rc.d

PACKAGECONFIG ??= "aws bird capture cli kube ssh tap wakeonlan"
PACKAGECONFIG[aws] = "ts_aws,ts_omit_aws"
PACKAGECONFIG[bird] = "ts_bird,ts_omit_bird"
PACKAGECONFIG[capture] = "ts_capture,ts_omit_capture"
PACKAGECONFIG[cli] = "ts_include_cli,ts_omit_include_cli"
PACKAGECONFIG[completion] = "ts_completion,ts_omit_completion"
PACKAGECONFIG[kube] = "ts_kube,ts_omit_kube"
PACKAGECONFIG[ssh] = "ts_ssh,ts_omit_ssh"
PACKAGECONFIG[tap] = "ts_tap,ts_omit_tap"
PACKAGECONFIG[wakeonlan] = "ts_wakeonlan,ts_omit_wakeonlan"

INITSCRIPT_PACKAGES = "${PN}d"
INITSCRIPT_NAME:${PN}d = "tailscaled"
INITSCRIPT_PARAMS:${PN}d = "defaults 91 9"

# override do_install, since it installs in bin instead of sbin
do_install() {
    install -d ${D}/${sbindir}
    install -m 0755 ${B}/${GO_BUILD_BINDIR}/tailscaled ${D}/${sbindir}/tailscaled

    if [ "${@bb.utils.contains('PACKAGECONFIG', 'cli', 'true', 'false', d)}" = 'true' ]; then
        install -d ${D}/${bindir}
        ln -sr ${D}${sbindir}/tailscaled ${D}${bindir}/tailscale
    fi

    install -d ${D}${sysconfdir}/default
    install -m 644 ${UNPACKDIR}/default ${D}${sysconfdir}/default/${BPN}d

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${UNPACKDIR}/tailscaled.init ${D}${sysconfdir}/init.d/tailscaled
    fi
}

PACKAGES =+ "${PN}d"

# mark these as src, since there are bash script etc in there and QA will complain otherwise
FILES:${PN}-src += "${libdir}/go/src"
FILES:${PN}d = "${sysconfdir}"

RDEPENDS:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'completion', 'bash-completion', '', d)}"
RDEPENDS:${PN}d = "iptables"

RRECOMMENDS:${PN}d = "kernel-module-wireguard"
