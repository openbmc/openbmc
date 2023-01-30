FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " file://pam.d/common-password \
	     file://pam.d/common-auth \
            "
