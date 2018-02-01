#
# Things we exlude fromw world testing within the reference distro
#

# qwt from meta-qt4, has poky-lsb QA warnings, qt4 for lsb only
EXCLUDE_FROM_WORLD_pn-qwt = "1"
# python-pyqt from meta-qt4 requires sip from meta-oe
EXCLUDE_FROM_WORLD_pn-python-pyqt = "1"
