#
# Things we exlude fromw world testing within the reference distro
#

# qwt from meta-qt4, has poky-lsb QA warnings, qt4 for lsb only
EXCLUDE_FROM_WORLD_pn-qwt = "1"
