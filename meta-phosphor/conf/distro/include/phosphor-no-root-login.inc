DISTRO_FEATURES += "phosphor-no-root-login"
DISTROOVERRIDES .= ":df-phosphor-no-root-login"

# The allow-root-login image feature drives two rootfs postprocess steps:
# the OE-core ssh_allow_root_login step and the phosphor 
# update_root_user_groups step. Removing the image feature disables both,
# so root can no longer log in over SSH and is not in the privileged
# groups.
IMAGE_FEATURES:remove = "allow-root-login"

# Lock the root account as well. usermod commands run in the order given, so
# this lock follows the password that phosphor-defaults.inc sets for root and
# wins.
EXTRA_USERS_PARAMS:append:pn-obmc-phosphor-image = " usermod --lock root;"
