FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	    file://lspci.cfg \
	    file://lsusb.cfg \
	    file://mdev.cfg \
	    file://mount-cifs.cfg \
	    file://ps-extras.cfg \
	    file://getopt.cfg \
           "
