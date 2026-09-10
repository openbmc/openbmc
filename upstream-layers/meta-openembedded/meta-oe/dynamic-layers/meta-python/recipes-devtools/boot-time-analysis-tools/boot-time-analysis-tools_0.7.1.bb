SUMMARY = "Boot time analysis tools from CentOS Automotive SIG"
DESCRIPTION = "Tools for analyzing and visualizing system boot time"
HOMEPAGE = "https://gitlab.com/CentOS/automotive/src/boot-time-analysis-tools"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1c2e0cc0dec0b709fe547806b55737b0"

SRC_URI = "git://gitlab.com/CentOS/automotive/src/boot-time-analysis-tools.git;protocol=https;branch=main;tag=${PV}"
SRCREV = "2409ce37e7389d8079bdb8740cb6a44b505fb8d4"

inherit setuptools3 features_check

# This tool reads from the systemd journal and queries D-Bus via systemd;
# it has no meaningful function without systemd as the init system.
REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS:${PN} = "python3-dbus ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'python3-systemd', '', d)}"
