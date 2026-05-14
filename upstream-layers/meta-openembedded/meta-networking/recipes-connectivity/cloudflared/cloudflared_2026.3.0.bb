SUMMARY = "Cloudflare Tunnel client"
DESCRIPTION = "cloudflared is the command-line client for Cloudflare Tunnel, \
a tunneling daemon that proxies traffic from the Cloudflare network to your origins."
HOMEPAGE = "https://github.com/cloudflare/cloudflared"
SECTION = "networking"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "git://github.com/cloudflare/cloudflared.git;protocol=https;branch=master;destsuffix=${GO_SRCURI_DESTSUFFIX} \
           file://cloudflared.service \
           file://default \
           "
SRCREV = "d2a87e9b93456ad7f82417400f4209d513668487"

GO_IMPORT = "github.com/cloudflare/cloudflared"
GO_INSTALL = "${GO_IMPORT}/cmd/cloudflared"
GO_LINKSHARED = ""
SRCREV_SHORT = "${@d.getVar('SRCREV')[:8]}"
GO_EXTRA_LDFLAGS = "-X main.Version=${PV}-${SRCREV_SHORT}"

inherit go-mod systemd

SYSTEMD_SERVICE:${PN} = "cloudflared.service"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/cloudflared.service ${D}${systemd_system_unitdir}/cloudflared.service
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/default ${D}${sysconfdir}/default/cloudflared
}

FILES:${PN}-src += "${libdir}/go/src"

# Fix shebang and QA Issue with scripts with /bin/bash and /usr/bin/python3
INSANE_SKIP:${PN}-dev = "file-rdeps"
