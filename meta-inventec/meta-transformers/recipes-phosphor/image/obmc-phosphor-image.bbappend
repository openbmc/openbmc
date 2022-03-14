inherit extrausers

EXTRA_USERS_PARAMS:append = " \
useradd -e '' -ou 0 -d /home/root -G priv-admin,root,sudo,ipmi,web,redfish -p 'gzW59equAcJAg' sysadmin; \
useradd -e '' -ou 0 -d /home/root -G priv-admin,root,sudo,ipmi,web,redfish -p 'kFdHdjRkot8KQ' admin; \
"
OBMC_IMAGE_EXTRA_INSTALL:append = " openssh-sftp-server"
OBMC_IMAGE_EXTRA_INSTALL:append = " phosphor-ipmi-ipmb"
OBMC_IMAGE_EXTRA_INSTALL:append = " python3-smbus"
OBMC_IMAGE_EXTRA_INSTALL:append = " ipmitool"
#BMC_IMAGE_EXTRA_INSTALL:append = " rest-dbus"
OBMC_IMAGE_EXTRA_INSTALL:append = " mmc-utils"
OBMC_IMAGE_EXTRA_INSTALL:append = " transformers-init"
