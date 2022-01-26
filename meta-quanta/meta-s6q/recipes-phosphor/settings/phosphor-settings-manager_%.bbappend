FILESEXTRAPATHS:prepend:s6q := "${THISDIR}/${PN}:"

SRC_URI:append:s6q = " file://chassis-capabilities.override.yml \
                       file://power-policy-set-default.override.yml \
                     "
