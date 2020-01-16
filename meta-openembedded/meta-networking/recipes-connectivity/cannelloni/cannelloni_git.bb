SUMMARY = "a SocketCAN over Ethernet tunnel"
HOMEPAGE = "https://github.com/mguentner/cannelloni"
LICENSE = "GPLv2"

SRC_URI = "git://github.com/mguentner/cannelloni.git;protocol=https \
           file://0001-Use-GNUInstallDirs-instead-of-hard-coding-paths.patch \
           file://0002-include-missing-stdexcept-for-runtime_error.patch \
          "
SRCREV = "82aa49b417b96fe46bb3f017ae1bfea928f20f9a"

PV = "20160414+${SRCPV}"

LIC_FILES_CHKSUM = "file://gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit cmake

S = "${WORKDIR}/git"

PACKAGECONFIG ??= "lksctp-tools"
PACKAGECONFIG[lksctp-tools] = "-DSCTP_SUPPORT=true, -DSCTP_SUPPORT=false, lksctp-tools"
