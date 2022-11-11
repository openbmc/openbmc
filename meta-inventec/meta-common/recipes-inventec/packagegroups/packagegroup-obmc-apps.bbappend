#Add python utility.(e.g gpioutil,...)
RDEPENDS:${PN}-extrasdevtools:append = " ipmitool"
RDEPENDS:${PN}-extrasdevtools:append = " openssh-sftp-server"
RDEPENDS:${PN}-extras += " python3-smbus"

#Install publickey for image file verification
RDEPENDS:${PN}-extras += " phosphor-image-signing"

RDEPENDS:${PN}-health-monitor:remove:transformers = "phosphor-health-monitor"
