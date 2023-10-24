FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Force the mctp-demux to be used until machine is ready to use in-kernel MCTP
PACKAGECONFIG = "transport-mctp-demux"
