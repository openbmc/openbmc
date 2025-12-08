DISTRO_FEATURES += "phosphor-no-ipmi-rmcp"
DISTROOVERRIDES += ":df-phosphor-no-ipmi-rmcp"

# libpam will disable IPMI modules when ipmi-rmcp is enabled, so we need to
# add pam-ipmi as a BAD_RECOMMENDATIONS.  Presently, these PAM modules are
# only used for phosphor-net-ipmid anyhow.
BAD_RECOMMENDATIONS:append = "pam-ipmi"
