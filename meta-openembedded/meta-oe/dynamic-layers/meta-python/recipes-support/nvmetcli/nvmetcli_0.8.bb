SUMMARY = "NVM-Express target user space configuration utility."
DESCRIPTION = "This package contains the command line interface to the NVMe \
over Fabrics nvmet in the Linux kernel.  It allows configuring the nvmet \
interactively as well as saving / restoring the configuration to / from a json \
file."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=1dece7821bf3fd70fe1309eaa37d52a2"

inherit systemd setuptools3

# nvmet service will start and stop the NVMe Target configuration on boot and
# shutdown from a saved NVMe Target configuration in the /etc/nvmet/config.json
# file. This file is not installed by default since the configuration will vary
# on real systems. Example configuration files are provided by including the
# nvmetcli-examples package.
SYSTEMD_SERVICE:${PN} = "nvmet.service"

SYSTEMD_AUTO_ENABLE ?= "disable"

RDEPENDS:${PN} += "python3 python3-six python3-pyparsing python3-configshell-fb"

SRCREV = "d323d5a3091af490502c5d025ec7361a3a2cfbd9"
SRC_URI = "git://git.infradead.org/users/hch/nvmetcli.git;branch=master"

S = "${WORKDIR}/git"

do_install:append() {
    # Install example configuration scripts.
    install -d ${D}${datadir}/nvmet
    cp -fr ${S}/examples ${D}${datadir}/nvmet/

    # Install systemd service file.
    install -d ${D}${systemd_unitdir}/system
    cp -fr ${S}/nvmet.service ${D}${systemd_unitdir}/system
}

# Examples package contains example json files used to configure nvmet.
PACKAGES += "${PN}-examples"
FILES:${PN}-examples = "${datadir}/nvmet/examples/*"
