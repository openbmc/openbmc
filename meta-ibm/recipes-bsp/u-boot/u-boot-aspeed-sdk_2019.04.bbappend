FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:ibm-enterprise = " file://ibm.json file://ips.json file://keys/"
SRC_URI:append:ibm-enterprise = " file://p10bmc.cfg"

OTPTOOL_CONFIGS:ibm-enterprise = "${UNPACKDIR}/ibm.json ${UNPACKDIR}/ips.json"
OTPTOOL_KEY_DIR:ibm-enterprise = "${UNPACKDIR}/keys/"

# !!! Do not copy ibm-enterprise's use of little-endian key ordering !!!
#
# The prefered order for production silicon is big-endian. Little-endian is necessary for IBM
# platforms due to development history involving pre-production AST2600 silicon. More discussion
# here:
#
# https://gerrit.openbmc-project.xyz/c/openbmc/openbmc/+/50716
SOCSEC_SIGN_EXTRA_OPTS = "--rsa_key_order=little"

do_deploy:prepend:ibm-enterprise() {
	# otptool needs access to the public and private socsec signing keys in the keys/ directory
	openssl rsa -in ${SOCSEC_SIGN_KEY} -pubout > ${UNPACKDIR}/keys/rsa_pub_oem_dss_key.pem
}
