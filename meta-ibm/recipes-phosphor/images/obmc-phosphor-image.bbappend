OBMC_IMAGE_EXTRA_INSTALL_append_ibm-ac-server = " mboxd max31785-msl phosphor-msl-verify liberation-fonts uart-render-controller first-boot-set-hostname"
OBMC_IMAGE_EXTRA_INSTALL_append_rainier = " mboxd ibmtpm2tss"
OBMC_IMAGE_EXTRA_INSTALL_append_witherspoon-tacoma = " ibmtpm2tss"
OBMC_IMAGE_EXTRA_INSTALL_append_mihawk = " mboxd liberation-fonts uart-render-controller "

# remove so things fit in available flash space
IMAGE_FEATURES_remove_witherspoon = "obmc-user-mgmt-ldap"
