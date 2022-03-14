SUMMARY = "YADRO Firmware update tool"
DESCRIPTION = "Command line tool for update firmware"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-fwupdate"
PR = "r1"
PV = "1.0+git${SRCPV}"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit pkgconfig meson

DEPENDS += " \
    sdbusplus \
    openssl \
"

PACKAGECONFIG[obmc-phosphor-image] = "-Dbmc-image-type=obmc-phosphor-image,,,,,intel-platforms"
PACKAGECONFIG[intel-platforms] = "-Dbmc-image-type=intel-platforms,,,,,obmc-phosphor-image"
PACKAGECONFIG[reboot-guard-support] = "-Dreboot-guard-support=true,-Dreboot-guard-support=false"
PACKAGECONFIG[openpower-support] = "-Dopenpower-support=true,-Dopenpower-support=false"

PACKAGECONFIG ??= " obmc-phosphor-image reboot-guard-support "
PACKAGECONFIG:append_df-openpower = "openpower-support"

SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-fwupdate;branch=master;protocol=https"
SRCREV = "eea7cadf0a5b49b3b9eb304b67494516469e96af"
S = "${WORKDIR}/git"
